package atoz.coroutines

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout

class TimeoutsTest {
    @Test
    fun fastLookupCompletes() = runTest {
        val clock = testScheduler.timeSource.markNow()
        assertEquals("found after 50 ms", lookupOrNull(ms = 50, limit = 200))
        assertEquals(50.milliseconds, clock.elapsedNow())
    }

    @Test
    fun slowLookupIsCutOffAtTheLimit() = runTest {
        val clock = testScheduler.timeSource.markNow()
        assertNull(lookupOrNull(ms = 5_000, limit = 200))
        assertEquals("timed out after 200 ms", lookupOrDefault(ms = 5_000, limit = 200))
        assertEquals(400.milliseconds, clock.elapsedNow()) // two timeouts, never the full 5 s
    }

    @Test
    fun timeoutIsACancellationException() = runTest {
        val e = assertFailsWith<TimeoutCancellationException> { withTimeout(10) { slowLookup(100) } }
        assertIs<CancellationException>(e)
    }
}
