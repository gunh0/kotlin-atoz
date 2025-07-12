package atoz.functional

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class FoldReduceTest {
    private val txns = listOf(Txn("t1", 100), Txn("t2", -30), Txn("t3", -90), Txn("t4", 50))

    @Test
    fun foldCarriesAnAccumulatorOfAnotherType() {
        assertEquals(30, balance(txns))
        assertEquals(1030, balance(txns, opening = 1000))
        assertEquals(4096, digitsToNumber(listOf(4, 0, 9, 6)))
    }

    @Test
    fun runningFoldKeepsIntermediateResults() {
        assertEquals(listOf(0L, 100L, 70L, -20L, 30L), runningBalances(txns))
        assertEquals("t3", firstOverdraft(txns))
        assertNull(firstOverdraft(txns.take(2)))
    }

    @Test
    fun reduceFailsOnEmptyCollections() {
        assertEquals("reduce", longestWord(listOf("fold", "reduce", "scan")))
        assertFailsWith<UnsupportedOperationException> { longestWord(emptyList()) }
        assertNull(longestWordOrNull(emptyList()))
    }

    @Test
    fun directionMatters() {
        assertEquals("(((a)b)c)", foldLeftText(listOf("a", "b", "c")))
        assertEquals("(a(b(c)))", foldRightText(listOf("a", "b", "c")))
    }
}
