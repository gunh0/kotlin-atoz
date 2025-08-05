package atoz.collections

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class IndexedAccessTest {
    private val lines = listOf("INFO start", "WARN slow", "ERROR failed", "INFO stop")

    @Test
    fun withIndexAndFilterIndexed() {
        assertEquals("3: ERROR failed", numbered(lines)[2])
        assertEquals(listOf("INFO start", "ERROR failed"), evenPositions(lines))
    }

    @Test
    fun safeIndexAccess() {
        assertEquals("--port", argumentOr(listOf("--port"), 0, "?"))
        assertEquals("?", argumentOr(listOf("--port"), 3, "?"))
        assertNull(lines.getOrNull(10))
        assertFailsWith<IndexOutOfBoundsException> { lines[10] }
    }

    @Test
    fun indexOfReturnsMinusOne() {
        assertEquals(2, firstErrorLine(lines))
        assertEquals(-1, firstErrorLine(listOf("INFO ok")))
    }

    @Test
    fun indicesAndLastIndex() {
        assertEquals(0..3, lines.indices)
        assertEquals(3, lines.lastIndex)
        assertEquals(listOf(1 to 5, 2 to 4), mirrorPairs(listOf(1, 2, 3, 4, 5)))
    }
}
