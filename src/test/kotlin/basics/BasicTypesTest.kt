package atoz.basics

import kotlin.test.Test
import kotlin.test.assertEquals

class BasicTypesTest {
    @Test
    fun literalsCarryTheirType() {
        assertEquals("Int", typeNameOf(1))
        assertEquals("Long", typeNameOf(1L))
        assertEquals("Double", typeNameOf(1.0))
        assertEquals("Float", typeNameOf(1f))
        assertEquals("Char", typeNameOf('k'))
    }

    @Test
    fun wideningIsExplicit() {
        assertEquals(2_147_483_648L, bytesFor(2048))
    }

    @Test
    fun integerDivisionTruncates() {
        assertEquals(3, integerAverage(7, 2))
        assertEquals(3.5, exactAverage(7, 2))
    }

    @Test
    fun narrowingKeepsLowBits() {
        assertEquals(1, narrowToInt(4_294_967_297L))
        assertEquals(-1, narrowToInt(0xFFFF_FFFFL))
    }

    @Test
    fun doubleToIntRoundsTowardZero() {
        assertEquals(3, truncate(3.99))
        assertEquals(-3, truncate(-3.99))
        assertEquals(0, truncate(Double.NaN))
    }

    @Test
    fun charsAreNotNumbers() {
        assertEquals(55, codeOf('7'))
        assertEquals(7, digitOf('7'))
        assertEquals(65, codeOf('A'))
    }
}
