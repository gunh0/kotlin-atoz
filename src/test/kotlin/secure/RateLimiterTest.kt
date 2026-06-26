package atoz.secure

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest

class RateLimiterTest {
    @Test
    fun burstUpToCapacityThenReject() = runTest {
        val bucket = TokenBucket(capacity = 3, refillEvery = 1.seconds, timeSource = testScheduler.timeSource)
        assertEquals(listOf(true, true, true, false, false), List(5) { bucket.tryAcquire() })
        delay(999.milliseconds)
        assertEquals(false, bucket.tryAcquire())
        delay(1.milliseconds)
        assertEquals(true, bucket.tryAcquire())
    }

    @Test
    fun refillIsCappedAtCapacity() = runTest {
        val bucket = TokenBucket(capacity = 3, refillEvery = 1.seconds, timeSource = testScheduler.timeSource)
        repeat(3) { bucket.tryAcquire() }
        delay(1.seconds * 60) // an idle minute does not bank 60 tokens
        assertEquals(3, bucket.available())
    }

    @Test
    fun acquireSuspendsUntilATokenIsDue() = runTest {
        val bucket = TokenBucket(capacity = 2, refillEvery = 100.milliseconds, timeSource = testScheduler.timeSource)
        val start = testScheduler.timeSource.markNow()
        repeat(5) { bucket.acquire() }
        assertEquals(300.milliseconds, start.elapsedNow()) // 2 from the burst, then one every 100 ms
    }

    @Test
    fun concurrentCallersAreSpacedOut() = runTest {
        val bucket = TokenBucket(capacity = 1, refillEvery = 10.seconds, timeSource = testScheduler.timeSource)
        val start = testScheduler.timeSource.markNow()
        val grantedAt = mutableListOf<Duration>()
        val callers = List(4) { launch { bucket.acquire(); grantedAt += start.elapsedNow() } }
        callers.forEach { it.join() }
        assertEquals(listOf(0, 10, 20, 30).map { it.seconds }, grantedAt.sorted())
    }

    @Test
    fun invalidConfiguration() {
        assertFailsWith<IllegalArgumentException> { TokenBucket(capacity = 0, refillEvery = 1.seconds) }
        assertFailsWith<IllegalArgumentException> { TokenBucket(capacity = 1, refillEvery = Duration.ZERO) }
    }
}
