package atoz.coroutines

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/*
Testing code with delays: `runTest` (kotlinx-coroutines-test) runs the test on
a scheduler with virtual time. `delay` does not sleep; the scheduler jumps
ahead, so a test of a 7-second backoff finishes in milliseconds and is fully
deterministic. On `testScheduler`, `timeSource` measures virtual time,
`advanceTimeBy(duration)` runs only what is due before that point and
`runCurrent()` what is due right now. (The TestScope shortcuts `currentTime`
and `advanceTimeBy(ms)` are still experimental in kotlinx-coroutines 1.10.)
Production code needs no test hooks beyond the usual ones: take the
CoroutineScope (or dispatcher) as a parameter instead of creating your own,
the way you would inject a java.time.Clock.
*/

suspend fun <T> retryWithBackoff(attempts: Int, initialDelayMs: Long, block: suspend (attempt: Int) -> T): T {
    var wait = initialDelayMs
    repeat(attempts - 1) { attempt ->
        try {
            return block(attempt)
        } catch (e: CancellationException) {
            throw e // a cancelled caller must not be retried
        } catch (e: Exception) {
            // fall through to the next attempt
        }
        delay(wait)
        wait *= 2
    }
    return block(attempts - 1) // the last failure propagates
}

class Heartbeat(private val scope: CoroutineScope, private val periodMs: Long) {
    var beats = 0
        private set

    fun start(): Job = scope.launch {
        while (true) {
            delay(periodMs)
            beats++
        }
    }
}

fun main() = runBlocking {
    val result = retryWithBackoff(attempts = 4, initialDelayMs = 100) { attempt ->
        println("attempt $attempt")
        check(attempt == 3) { "not yet" }
        "ok on attempt $attempt"
    }
    println(result)
}
