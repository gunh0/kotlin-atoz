package atoz.coroutines

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest

class BoundedConcurrencyTest {
    @Test
    fun semaphoreCapsCallsInFlight() = runTest {
        val gauge = InFlightGauge()
        val start = testScheduler.timeSource.markNow()
        callWithSemaphore(requests = 10, permits = 3, gauge = gauge)
        assertEquals(3, gauge.max)
        assertEquals(400.milliseconds, start.elapsedNow()) // 4 waves of 100 ms
    }

    @Test
    fun limitedParallelismDoesNotCapSuspendedCalls() = runTest {
        val gauge = InFlightGauge()
        val start = testScheduler.timeSource.markNow()
        callOnLimitedDispatcher(10, StandardTestDispatcher(testScheduler), parallelism = 2, gauge = gauge)
        assertEquals(10, gauge.max) // every call suspended in delay at the same time
        assertEquals(100.milliseconds, start.elapsedNow())
    }

    @Test
    fun limitedParallelismCapsBlockingWork() = runTest {
        val gauge = InFlightGauge()
        blockOnLimitedDispatcher(tasks = 8, parallelism = 2, gauge = gauge)
        assertTrue(gauge.max <= 2, "max was ${gauge.max}")
    }
}
