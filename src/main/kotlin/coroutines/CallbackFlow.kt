package atoz.coroutines

import java.util.concurrent.CopyOnWriteArrayList
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield

/*
`callbackFlow { }` turns a listener API (register a callback, unregister it
later) into a cold Flow. In Java you implement the Listener and must remember
to remove it; here the flow owns the registration:
- each collector registers its own listener when collection starts;
- the callback pushes values with `trySend`, which never suspends and is safe
  from any thread (values wait in a buffer, 64 by default);
- `close(error)` from the callback ends the flow with that exception;
- `awaitClose { unregister }` keeps the flow open and runs the cleanup when the
  collector stops (`take(n)`, `first()`, cancellation), so nothing leaks.
Forgetting `awaitClose` is caught at runtime: the flow fails with
IllegalStateException as soon as the block returns, and the listener leaks.
*/

interface LoginEventListener {
    fun onEvent(event: String)
    fun onError(error: Throwable)
}

class LoginEventSource {
    private val listeners = CopyOnWriteArrayList<LoginEventListener>()

    val listenerCount: Int get() = listeners.size

    fun register(listener: LoginEventListener) {
        listeners += listener
    }

    fun unregister(listener: LoginEventListener) {
        listeners -= listener
    }

    fun publish(event: String) = listeners.forEach { it.onEvent(event) }

    fun fail(error: Throwable) = listeners.forEach { it.onError(error) }
}

fun LoginEventSource.events(): Flow<String> = callbackFlow {
    val listener = object : LoginEventListener {
        override fun onEvent(event: String) {
            trySend(event)
        }

        override fun onError(error: Throwable) {
            close(error)
        }
    }
    register(listener)
    awaitClose { unregister(listener) }
}

fun LoginEventSource.eventsWithoutAwaitClose(): Flow<String> = callbackFlow {
    register(object : LoginEventListener {
        override fun onEvent(event: String) {
            trySend(event)
        }

        override fun onError(error: Throwable) {
            close(error)
        }
    })
}

fun main() = runBlocking {
    val source = LoginEventSource()
    val collector = launch { println("received: ${source.events().take(2).toList()}") }
    while (source.listenerCount == 0) yield() // wait until the collector has registered
    println("listeners while collecting: ${source.listenerCount}")
    source.publish("alice logged in")
    source.publish("bob failed")
    collector.join()
    println("listeners afterwards: ${source.listenerCount}")
    println(runCatching { source.eventsWithoutAwaitClose().toList() }.exceptionOrNull()?.javaClass?.simpleName)
}
