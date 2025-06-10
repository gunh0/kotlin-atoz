package atoz.coroutines

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest

class FlowBasicsTest {
    @Test
    fun flowIsColdAndRunsPerCollector() = runTest {
        val clock = testScheduler.timeSource.markNow()
        val log = mutableListOf<String>()
        val flow = countdown(3, log)
        assertTrue(log.isEmpty())
        assertEquals(listOf(3, 2, 1), flow.toList())
        assertEquals(listOf(3, 2, 1), flow.toList())
        assertEquals(2, log.count { it == "started" })
        assertEquals(600.milliseconds, clock.elapsedNow())
    }

    @Test
    fun firstCancelsTheProducer() = runTest {
        val log = mutableListOf<String>()
        assertEquals(5, countdown(5, log).first())
        assertEquals(listOf("started", "emit 5"), log)
    }

    @Test
    fun operatorsChain() = runTest {
        assertEquals(listOf(4, 16), evenSquares(1, 2, 3, 4).toList())
    }
}
