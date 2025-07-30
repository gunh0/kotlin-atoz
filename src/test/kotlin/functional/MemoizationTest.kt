package atoz.functional

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class MemoizationTest {
    @Test
    fun memoizedFunctionComputesEachArgumentOnce() {
        var calls = 0
        val square = memoize { x: Int -> calls++; x * x }
        assertEquals(144, square(12))
        assertEquals(144, square(12))
        assertEquals(9, square(3))
        assertEquals(2, calls)
    }

    @Test
    fun recursiveMemoizationWithGetOrPut() {
        val fib = Fibonacci()
        assertEquals(2_880_067_194_370_816_120, fib.of(90))
        assertEquals(91, fib.computations)
    }

    @Test
    fun computeIfAbsentRejectsRecursiveUpdates() {
        assertEquals(1, fibWithComputeIfAbsent(1))
        assertFailsWith<ConcurrentModificationException> { fibWithComputeIfAbsent(20) }
    }
}
