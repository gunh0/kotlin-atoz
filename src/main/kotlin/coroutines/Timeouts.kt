package atoz.coroutines

import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull

/*
Timeouts: `withTimeout(ms) { }` cancels the block when the time is up and
throws TimeoutCancellationException; `withTimeoutOrNull` returns null instead.
Both rely on cancellation, so the block must be cancellable (see the previous
unit) for the timeout to take effect.

The Java equivalent is `future.get(timeout, unit)`, which leaves the task
running unless you also cancel it. Pitfall: TimeoutCancellationException is a
CancellationException, so thrown out of a `launch` it silently ends that
coroutine instead of being reported. Catch it close to `withTimeout`, or use
`withTimeoutOrNull`.
*/

suspend fun slowLookup(ms: Long): String {
    delay(ms)
    return "found after $ms ms"
}

suspend fun lookupOrNull(ms: Long, limit: Long): String? = withTimeoutOrNull(limit) { slowLookup(ms) }

suspend fun lookupOrDefault(ms: Long, limit: Long): String =
    try {
        withTimeout(limit) { slowLookup(ms) }
    } catch (e: TimeoutCancellationException) {
        "timed out after $limit ms"
    }

fun main() = runBlocking {
    println(lookupOrNull(ms = 50, limit = 200))
    println(lookupOrNull(ms = 500, limit = 200))
    println(lookupOrDefault(ms = 500, limit = 200))
}
