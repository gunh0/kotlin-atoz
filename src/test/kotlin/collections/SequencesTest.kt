package atoz.collections

import kotlin.test.Test
import kotlin.test.assertEquals

class SequencesTest {
    @Test
    fun listsProcessStageByStage() {
        val log = mutableListOf<String>()
        assertEquals(20, eagerTrace(listOf(1, 2, 3), log))
        assertEquals(listOf("map 1", "map 2", "map 3", "filter 10", "filter 20", "filter 30"), log)
    }

    @Test
    fun sequencesProcessElementByElementAndStopEarly() {
        val log = mutableListOf<String>()
        assertEquals(20, lazyTrace(listOf(1, 2, 3), log))
        assertEquals(listOf("map 1", "filter 10", "map 2", "filter 20"), log)
    }

    @Test
    fun infiniteSources() {
        assertEquals(listOf(0L, 1L, 1L, 2L, 3L, 5L, 8L, 13L), fibonacci().take(8).toList())
        assertEquals(listOf(100L, 200L, 400L, 800L, 1600L, 3200L), backoffDelays(100, 5_000))
    }

    @Test
    fun sequenceCanBeIteratedAgain() {
        val seq = fibonacci().take(3)
        assertEquals(seq.toList(), seq.toList())
    }
}
