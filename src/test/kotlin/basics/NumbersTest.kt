package atoz.basics

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class NumbersTest {
    @Test
    fun overflowWrapsUnlessChecked() {
        assertEquals(Int.MIN_VALUE, wrappingAdd(Int.MAX_VALUE, 1))
        assertFailsWith<ArithmeticException> { checkedAdd(Int.MAX_VALUE, 1) }
        assertEquals(2_147_483_648L, safeTotal(Int.MAX_VALUE, 1))
    }

    @Test
    fun divisionByZero() {
        assertFailsWith<ArithmeticException> { intDivide(1, 0) }
        assertEquals(Double.POSITIVE_INFINITY, doubleDivide(1.0, 0.0))
        assertEquals(Double.NaN, doubleDivide(0.0, 0.0))
    }

    @Test
    fun remainderVersusMod() {
        assertEquals(-1, remainder(-7, 3))
        assertEquals(2, bucket(-7, 3))
    }

    @Test
    fun bytesAreSignedUnsignedTypesAreNot() {
        assertEquals((-1).toByte(), signedByte(0xFF))
        assertEquals(255, unsignedValue(signedByte(0xFF)))
        assertEquals(UInt.MAX_VALUE, 0u - 1u)
        assertEquals("4294967295", UInt.MAX_VALUE.toString())
        assertEquals("0aff7f", hexOf(byteArrayOf(0x0A, -1, 0x7F)))
    }
}
