package atoz.coroutines

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.test.runTest

class VirtualTimeTestingTest {
    @Test
    fun backoffDoublesTheDelay() = runTest {
        val clock = testScheduler.timeSource.markNow()
        val calls = mutableListOf<Duration>()
        val result = retryWithBackoff(attempts = 4, initialDelayMs = 100) { attempt ->
            calls += clock.elapsedNow()
            check(attempt == 3)
            "ok"
        }
        assertEquals("ok", result)
        // waits 100, 200, 400 virtual ms; the test itself takes no real time
        assertEquals(listOf(0, 100, 300, 700).map { it.milliseconds }, calls)
    }

    @Test
    fun lastFailurePropagates() = runTest {
        val clock = testScheduler.timeSource.markNow()
        assertFailsWith<IllegalStateException> {
            retryWithBackoff(attempts = 3, initialDelayMs = 1_000) { error("down") }
        }
        assertEquals(3.seconds, clock.elapsedNow())
    }

    @Test
    fun advanceTimeByRunsOnlyWhatIsDue() = runTest {
        val heartbeat = Heartbeat(backgroundScope, periodMs = 1_000) // cancelled when the test ends
        heartbeat.start()
        testScheduler.advanceTimeBy(2_500.milliseconds)
        assertEquals(2, heartbeat.beats)
        testScheduler.advanceTimeBy(500.milliseconds) // now at 3000; the beat due at 3000 has not run yet
        assertEquals(2, heartbeat.beats)
        testScheduler.runCurrent()
        assertEquals(3, heartbeat.beats)
    }
}
