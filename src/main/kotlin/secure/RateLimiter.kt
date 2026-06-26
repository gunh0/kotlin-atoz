package atoz.secure

import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeSource
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

/*
A token bucket limits how often something may happen (login attempts, OTP
sends, outgoing webhook calls) while still allowing short bursts: the bucket
holds up to `capacity` tokens, refills one every `refillEvery`, and each call
takes one.
- `tryAcquire()` answers immediately: reject the request (HTTP 429).
- `acquire()` suspends until a token is available: throttle our own outgoing
  calls. It waits with `delay`, so no thread is blocked meanwhile.
Time comes from an injected kotlin.time.TimeSource. In production that is
TimeSource.Monotonic (immune to wall-clock jumps); in tests it is the virtual
clock of runTest (`testScheduler.timeSource`), so a test of a 10-second refill
runs in milliseconds. The lock is never held across a suspension.
*/

class TokenBucket(
    private val capacity: Int,
    private val refillEvery: Duration,
    private val timeSource: TimeSource = TimeSource.Monotonic,
) {
    private val lock = Any()
    private var tokens = capacity.toDouble()
    private var lastRefill = timeSource.markNow()

    init {
        require(capacity >= 1) { "capacity must be at least 1" }
        require(refillEvery.isPositive()) { "refill interval must be positive" }
    }

    fun tryAcquire(): Boolean = synchronized(lock) {
        refill()
        if (tokens >= 1) {
            tokens -= 1
            true
        } else {
            false
        }
    }

    suspend fun acquire() {
        while (true) {
            val wait = synchronized(lock) {
                refill()
                if (tokens >= 1) {
                    tokens -= 1
                    return
                }
                refillEvery * (1 - tokens)
            }
            delay(wait)
        }
    }

    fun available(): Int = synchronized(lock) {
        refill()
        tokens.toInt()
    }

    private fun refill() {
        val elapsed = lastRefill.elapsedNow()
        tokens = minOf(capacity.toDouble(), tokens + elapsed / refillEvery)
        lastRefill += elapsed
    }
}

fun main() = runBlocking {
    val logins = TokenBucket(capacity = 3, refillEvery = 1_000.milliseconds)
    println("login attempts: ${List(5) { logins.tryAcquire() }}") // a burst of 3, then rejected

    val outgoing = TokenBucket(capacity = 1, refillEvery = 50.milliseconds)
    val start = TimeSource.Monotonic.markNow()
    repeat(4) { outgoing.acquire() }
    println("4 throttled calls took about ${start.elapsedNow().inWholeMilliseconds} ms (>= 150)")
}
