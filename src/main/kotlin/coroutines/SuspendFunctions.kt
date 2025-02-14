package atoz.coroutines

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

/*
Suspend functions: a `suspend fun` can pause without blocking its thread.
The compiler rewrites it into a state machine with an extra Continuation
parameter (from Java it looks like `Object fetchUserName(int, Continuation)`),
so the code reads top to bottom like blocking Java code while the thread is
released at every suspension point such as `delay`.

A suspend function can only be called from another suspend function or from
a coroutine builder. `runBlocking` is the bridge from ordinary code: it starts
a coroutine and blocks the calling thread until it completes. Fine for `main`
and quick scripts, wrong inside other coroutines or request handlers.
*/

suspend fun fetchUserName(id: Int): String {
    delay(100) // suspends; the thread is free to run other coroutines meanwhile
    return "user-$id"
}

suspend fun fetchGreeting(id: Int): String {
    val name = fetchUserName(id) // calls are sequential by default, like normal code
    return "Hello, $name"
}

fun greetBlocking(id: Int): String = runBlocking { fetchGreeting(id) }

fun main() {
    println(greetBlocking(1))
    runBlocking {
        val names = (1..3).map { fetchUserName(it) } // map's lambda is inline, so it may suspend
        println("Sequential calls: $names")
    }
}
