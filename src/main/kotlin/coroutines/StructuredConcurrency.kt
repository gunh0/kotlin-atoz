package atoz.coroutines

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/*
Structured concurrency: every coroutine is started in a CoroutineScope and
becomes a child of that scope's Job. `coroutineScope { }` returns only after
all of its children have finished, and if one child fails, the remaining
siblings are cancelled and the exception is rethrown to the caller.
Nothing keeps running in the background after the function returns.
In Java, a task submitted to an ExecutorService is tied to nothing, so a
failure or an early return leaks the others (StructuredTaskScope is still a
preview API in Java 21).
*/

suspend fun loadDashboard(log: MutableList<String>): List<String> = coroutineScope {
    launch {
        delay(300)
        log += "audit written"
    }
    val profile = async { delay(100); "profile" }
    val alerts = async { delay(200); "alerts" }
    listOf(profile.await(), alerts.await())
} // the scope still waits for the audit launch before returning

suspend fun failingSiblings(log: MutableList<String>): Unit = coroutineScope {
    launch {
        try {
            delay(1_000)
            log += "slow finished"
        } catch (e: CancellationException) {
            log += "slow cancelled"
            throw e // always rethrow cancellation
        }
    }
    launch {
        delay(100)
        throw IllegalStateException("fast failed")
    }
}

fun main() = runBlocking {
    val log = mutableListOf<String>()
    println("dashboard: ${loadDashboard(log)}, log: $log")
    val failure = runCatching { failingSiblings(log) }.exceptionOrNull()
    println("failure: ${failure?.message}, log: $log")
}
