package atoz.collections

import kotlin.test.Test
import kotlin.test.assertEquals

class WindowingTest {
    @Test
    fun chunkedKeepsAShorterLastBlock() {
        assertEquals(listOf(listOf(1, 2, 3), listOf(4, 5, 6), listOf(7)), batches((1..7).toList(), 3))
    }

    @Test
    fun windowedSlides() {
        assertEquals(listOf(2.0, 3.0, 4.0), movingAverage(listOf(1.0, 2.0, 3.0, 4.0, 5.0), 3))
        assertEquals(listOf(listOf(1, 2, 3), listOf(3, 4, 5)), (1..6).toList().windowed(3, step = 2))
        assertEquals(
            listOf(listOf(1, 2, 3), listOf(3, 4, 5), listOf(5, 6)),
            (1..6).toList().windowed(3, step = 2, partialWindows = true),
        )
        assertEquals(listOf(1), bruteForceWindows(listOf(true, false, false, false, true, false), 3))
    }

    @Test
    fun zipFamily() {
        assertEquals(listOf(3, 0, -4), deltas(listOf(10, 13, 13, 9)))
        assertEquals(listOf("a=1", "b=2"), labelled(listOf("a", "b", "c"), listOf(1, 2)))
        assertEquals(listOf("x", "y") to listOf(1, 2), splitPairs(listOf("x" to 1, "y" to 2)))
    }
}
