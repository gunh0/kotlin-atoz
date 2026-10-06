package atoz.coroutines

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.channels.ClosedSendChannelException
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest

class GracefulShutdownTest {
    @Test
    fun queuedWorkFinishesWithinTheGracePeriod() = runTest {
        val pool = WorkerPool(backgroundScope, workers = 2) { delay(100) }
        listOf("a", "b", "c", "d").forEach { pool.submit(it) }
        val start = testScheduler.timeSource.markNow()
        val report = pool.shutdown(grace = 1.seconds)
        assertTrue(report.graceful)
        assertEquals(listOf("a", "b", "c", "d"), report.completed.sorted())
        assertEquals(emptyList(), report.interrupted + report.neverStarted)
        assertEquals(200.milliseconds, start.elapsedNow()) // returns as soon as the queue is drained
    }

    @Test
    fun pastTheDeadlineWorkersAreCancelledAndNothingIsLostSilently() = runTest {
        val pool = WorkerPool(backgroundScope, workers = 2) { delay(100) }
        listOf("a", "b", "c", "d", "e", "f").forEach { pool.submit(it) }
        val start = testScheduler.timeSource.markNow()
        val report = pool.shutdown(grace = 150.milliseconds)
        assertEquals(false, report.graceful)
        assertEquals(listOf("a", "b"), report.completed.sorted()) // done at 100 ms
        assertEquals(listOf("c", "d"), report.interrupted.sorted()) // started at 100 ms, cancelled at 150 ms
        assertEquals(listOf("e", "f"), report.neverStarted)
        assertEquals(150.milliseconds, start.elapsedNow())
    }

    @Test
    fun submitAfterShutdownFails() = runTest {
        val pool = WorkerPool(backgroundScope, workers = 1) { }
        pool.shutdown(grace = 1.seconds)
        assertFailsWith<ClosedSendChannelException> { pool.submit("late") }
    }
}
