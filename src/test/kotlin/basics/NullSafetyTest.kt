package atoz.basics

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class NullSafetyTest {
    @Test
    fun safeCallAndElvis() {
        assertEquals(0, lengthOrZero(null))
        assertEquals(3, lengthOrZero("abc"))
    }

    @Test
    fun smartCastAfterNullCheck() {
        assertEquals("(nothing)", shout(null))
        assertEquals("KOTLIN", shout("kotlin"))
        assertEquals("holds 3 chars", NullableHolder("abc").describe())
        assertEquals("empty", NullableHolder(null).describe())
    }

    @Test
    fun elvisCanThrow() {
        assertEquals("ian", requireUsername("  ian "))
        assertFailsWith<IllegalArgumentException> { requireUsername("   ") }
        assertFailsWith<IllegalArgumentException> { requireUsername(null) }
    }

    @Test
    fun doubleBangThrowsNpe() {
        assertEquals(2, forcedLength("ok"))
        assertFailsWith<NullPointerException> { forcedLength(null) }
    }

    @Test
    fun nullableElementsVersusNullableList() {
        assertEquals(2, countPresent(listOf("a", null, "b")))
        assertEquals(-1, sizeOrMinusOne(null))
        assertEquals(1, sizeOrMinusOne(listOf("a")))
    }
}
