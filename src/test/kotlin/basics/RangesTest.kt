package atoz.basics

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RangesTest {
    @Test
    fun closedAndHalfOpen() {
        assertEquals(listOf(1, 2, 3, 4, 5), closed())
        assertEquals(listOf(1, 2, 3, 4), halfOpen())
    }

    @Test
    fun downToAndStep() {
        assertEquals(listOf(10, 7, 4, 1), countdown())
        assertEquals(listOf(1, 5, 9), everyFourth().toList())
        assertEquals(9, everyFourth().last)
    }

    @Test
    fun reversedBoundsMakeAnEmptyRange() {
        assertTrue((5..1).isEmpty())
        assertEquals(listOf(5, 4, 3, 2, 1), (5 downTo 1).toList())
    }

    @Test
    fun membership() {
        assertEquals("abcde", letters())
        assertTrue(isWorkingHour(9))
        assertFalse(isWorkingHour(18))
        assertTrue(isPrintableAscii('~'))
        assertFalse(isPrintableAscii('\n'))
        assertTrue(isProbability(0.5))
        assertFalse(isProbability(1.01))
    }
}
