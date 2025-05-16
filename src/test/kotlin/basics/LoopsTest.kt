package atoz.basics

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class LoopsTest {
    @Test
    fun labelledBreakLeavesBothLoops() {
        assertEquals(1 to 9, firstPairSummingTo(listOf(1, 4, 6, 9), 10))
        assertNull(firstPairSummingTo(listOf(1, 2), 10))
    }

    @Test
    fun labelledContinueSkipsTheOuterIteration() {
        assertEquals(listOf(3, 9), sumsOfRowsWithoutZero(listOf(listOf(1, 2), listOf(3, 0), listOf(4, 5))))
    }

    @Test
    fun whileAndDoWhile() {
        assertEquals(111, collatzSteps(27))
        assertEquals(1, digitCount(0))
        assertEquals(5, digitCount(12345))
    }

    @Test
    fun returnAtLabelActsLikeContinue() {
        assertEquals(listOf(2, 4, 6), evensKeptByForEach(listOf(1, 2, 3, 4, 5, 6)))
    }
}
