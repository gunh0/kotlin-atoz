package atoz.functional

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class TailRecursionTest {
    @Test
    fun bothVersionsAgreeOnSmallInputs() {
        assertEquals(5050, naiveSum(100))
        assertEquals(5050, tailSum(100))
    }

    @Test
    fun tailrecRunsInConstantStack() {
        assertEquals(500_000_500_000, tailSum(1_000_000))
    }

    @Test
    fun plainRecursionOverflowsTheStack() {
        assertFailsWith<StackOverflowError> { naiveSum(10_000_000) }
    }

    @Test
    fun otherTailRecursiveFunctions() {
        assertEquals(21, gcd(1071, 462))
        assertEquals(4, lastMatching(listOf(1, 2, 3, 4, 5)) { it % 2 == 0 })
        assertNull(lastMatching(listOf(1, 3)) { it % 2 == 0 })
    }
}
