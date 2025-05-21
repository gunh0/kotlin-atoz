package atoz.coroutines

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope

/*
Exceptions in coroutines follow the Job hierarchy, not the call stack:
- A failing `launch` cancels its parent, which cancels all siblings.
- A failing `async` stores the exception and rethrows it from `await()`; inside
  `coroutineScope` it still cancels the scope even if you catch it at `await`.
- `supervisorScope` (or a SupervisorJob) lets children fail independently.
- CoroutineExceptionHandler is the last resort for uncaught exceptions; it is
  only consulted by root coroutines and direct children of a supervisor,
  much like Thread.UncaughtExceptionHandler. It does not make them recover.
*/

suspend fun supervisedTasks(log: MutableList<String>): Unit = supervisorScope {
    val handler = CoroutineExceptionHandler { _, e -> log += "handled: ${e.message}" }
    launch(handler) {
        delay(10)
        throw IllegalStateException("task A failed")
    }
    launch {
        delay(20)
        log += "task B finished" // the sibling survives
    }
}

suspend fun awaitInSupervisor(): String = supervisorScope {
    val deferred = async<String> { throw IllegalArgumentException("bad input") }
    try {
        deferred.await()
    } catch (e: IllegalArgumentException) {
        "recovered: ${e.message}"
    }
}

suspend fun awaitInCoroutineScope(): String = coroutineScope {
    val deferred = async<String> { throw IllegalArgumentException("bad input") }
    try {
        deferred.await()
    } catch (e: IllegalArgumentException) {
        "recovered: ${e.message}" // too late: the failed child already cancelled this scope
    }
}

fun main() = runBlocking {
    val log = mutableListOf<String>()
    supervisedTasks(log)
    println(log)
    println(awaitInSupervisor())
    println(runCatching { awaitInCoroutineScope() })
}
