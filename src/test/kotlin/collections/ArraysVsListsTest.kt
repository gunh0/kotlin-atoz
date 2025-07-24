package atoz.collections

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ArraysVsListsTest {
    @Test
    fun arraysCompareByReference() {
        val a = intArrayOf(1, 2, 3)
        val b = intArrayOf(1, 2, 3)
        assertFalse(arraysEqualByReference(a, b))
        assertTrue(arraysEqualByContent(a, b))
        assertTrue(listsEqual(a.toList(), b.toList()))
    }

    @Test
    fun contentToString() {
        assertEquals("[1, 2, 3]", describeArray(intArrayOf(1, 2, 3)))
    }

    @Test
    fun arrayOfIsBoxedIntArrayIsPrimitive() {
        assertEquals("Integer[]" to "int[]", boxedVersusPrimitive())
    }

    @Test
    fun secretIsWipedAfterUse() {
        val raw = "hunter2".encodeToByteArray()
        assertEquals(7, Secret(raw).use { it.size })
        assertTrue(raw.all { it == 0.toByte() })
    }
}
