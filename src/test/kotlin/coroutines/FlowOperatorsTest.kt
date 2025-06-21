package atoz.coroutines

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest

class FlowOperatorsTest {
    @Test
    fun zipPairsByPosition() = runTest {
        assertEquals(listOf("1a", "2b"), zipped().toList())
    }

    @Test
    fun combineUsesLatestOfEach() = runTest {
        // a: 1@100 2@200 3@300, b: 1@160 2@320; nothing is emitted before both sides have a value
        assertEquals(listOf("1/1", "2/1", "3/1", "3/2"), combinedLatest().toList())
    }

    @Test
    fun bufferOverlapsProducerAndCollector() = runTest {
        var clock = testScheduler.timeSource.markNow()
        assertEquals(listOf(1, 2, 3), collectSlowly(ticks(3, 100), workMs = 300))
        assertEquals(1_200.milliseconds, clock.elapsedNow()) // 3 x (100 + 300), taking turns
        clock = testScheduler.timeSource.markNow()
        assertEquals(listOf(1, 2, 3), collectSlowly(ticks(3, 100).buffer(), workMs = 300))
        assertEquals(1_000.milliseconds, clock.elapsedNow()) // 100 + 3 x 300
    }

    @Test
    fun conflateDropsStaleValues() = runTest {
        assertEquals(listOf(1, 3, 5), collectSlowly(ticks(5, 100).conflate(), workMs = 250))
    }
}
