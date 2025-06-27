package atoz.generics

import kotlin.test.Test
import kotlin.test.assertEquals

class UseSiteVarianceTest {
    @Test
    fun outAndInProjectionsTogether() {
        val numbers: MutableList<Number> = mutableListOf(1.5)
        assertEquals(2, copyInto(arrayOf(1, 2), numbers))
        assertEquals(listOf<Number>(1.5, 1, 2), numbers)
    }

    @Test
    fun outProjectionReads() {
        val ints: MutableList<Int> = mutableListOf(1, 2, 3)
        assertEquals(6.0, sumAll(ints))
    }

    @Test
    fun inProjectionWrites() {
        val anything: MutableList<Any> = mutableListOf("start")
        addDefaults(anything)
        assertEquals(listOf<Any>("start", 80, 443), anything)
    }

    @Test
    fun starProjection() {
        assertEquals("Int, String, null, Double", describeUnknown(listOf(1, "a", null, 2.0)))
        val list = mutableListOf("x", "y")
        assertEquals(2, clearAny(list))
        assertEquals(emptyList(), list)
    }
}
