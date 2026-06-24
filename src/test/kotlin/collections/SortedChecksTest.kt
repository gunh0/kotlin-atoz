package atoz.collections

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SortedChecksTest {
    @Test
    fun sortedMeansNonDecreasing() {
        assertTrue(listOf(1, 1, 2).isSorted())
        assertFalse(listOf(1, 1, 2).isSortedDescending())
        assertTrue(emptyList<Int>().isSorted())
        assertTrue(intArrayOf(9, 5, 5).isSortedDescending())
        assertFalse(listOf("a", "B", "c").isSorted()) // natural String order puts "B" before "a"
        assertTrue(listOf("a", "B", "c").isSortedWith(String.CASE_INSENSITIVE_ORDER))
        assertTrue(listOf("ccc", "bb", "a").isSortedByDescending { it.length })
    }

    @Test
    fun selectorAndStrictness() {
        val log = listOf(AuditRecord(1, 100, "login"), AuditRecord(2, 100, "read"), AuditRecord(2, 105, "delete"))
        assertTrue(isChronological(log))
        assertFalse(hasStrictlyIncreasingSequence(log)) // replayed sequence number 2
        assertFalse(isChronological(log.reversed()))
        assertTrue(hasStrictlyIncreasingSequence(listOf(AuditRecord(1, 0, "a"), AuditRecord(7, 0, "b"))))
    }

    @Test
    fun stopsAtTheFirstViolationOnSequences() {
        var pulled = 0
        val sorted = generateSequence(1) { it + 1 }.map { if (it == 3) 0 else it }.onEach { pulled++ }.isSorted()
        assertFalse(sorted)
        assertEquals(3, pulled) // 1, 2, 0: an infinite sequence is fine
    }

    @Test
    fun binarySearchNeedsSortedInput() {
        assertTrue(listOf(443, 22, 80).binarySearch(443) < 0) // present, but not found
        assertFailsWith<IllegalArgumentException> { indexOfBlockedPort(listOf(443, 22, 80), 443) }
        assertEquals(0, indexOfBlockedPort(listOf(22, 80, 443), 22))
    }
}
