package atoz.coroutines

import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/*
Cancellation is cooperative. `job.cancel()` only marks the job as cancelling;
the coroutine notices at its next suspension point (every kotlinx suspend
function checks) or when it checks `isActive` / calls `ensureActive()`.
A CPU-bound loop that never suspends or checks keeps running, just like a Java
thread that never looks at `Thread.interrupted()`.

Cancellation arrives as a CancellationException, so `finally` blocks run.
Cleanup that itself has to suspend must be wrapped in
`withContext(NonCancellable)`; otherwise its first suspension throws again.
Never swallow CancellationException in a catch-all.
*/

suspend fun busyLoop(iterations: Int, cooperative: Boolean): Int {
    var done = 0
    repeat(iterations) {
        if (cooperative) currentCoroutineContext().ensureActive()
        done++ // no suspension here, so nothing else would notice the cancel
    }
    return done
}

suspend fun workWithCleanup(log: MutableList<String>, protectCleanup: Boolean) {
    try {
        delay(1_000)
        log += "work finished"
    } finally {
        if (protectCleanup) {
            withContext(NonCancellable) {
                delay(10) // e.g. flushing a buffer or releasing a remote lock
                log += "cleanup done"
            }
        } else {
            delay(10) // throws CancellationException right away
            log += "cleanup done"
        }
    }
}

fun main() = runBlocking {
    launch {
        coroutineContext.job.cancel()
        println("ignoring cancel: ${busyLoop(1_000, cooperative = false)} iterations")
        println("checking: ${runCatching { busyLoop(1_000, cooperative = true) }.exceptionOrNull()}")
    }.join()

    for (protect in listOf(false, true)) {
        val log = mutableListOf<String>()
        val job = launch { workWithCleanup(log, protect) }
        delay(100)
        job.cancelAndJoin()
        println("NonCancellable=$protect -> $log")
    }
}
