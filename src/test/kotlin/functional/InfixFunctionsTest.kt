package atoz.functional

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class InfixFunctionsTest {
    @Test
    fun customInfixFunctions() {
        assertEquals(30, 15 percentOf 200)
        assertEquals("*******", "hunter2" redactedWith '*')
    }

    @Test
    fun infixMembersReadLikeRules() {
        val rw = Permission.READ or Permission.WRITE
        assertTrue(rw allows Permission.READ)
        assertFalse(rw allows Permission.EXECUTE)
        assertEquals(Permission.WRITE, rw and Permission.WRITE)
    }

    @Test
    fun bitwiseOperatorsAreInfixFunctions() {
        val flags = flagsDemo()
        assertEquals(16, flags["1 shl 4"])
        assertEquals(0b1000, flags["0b1100 and 0b1010"])
        assertEquals(0b1110, flags["0b1100 or 0b1010"])
        assertEquals(0b0110, flags["0b1100 xor 0b1010"])
        assertEquals(-4, flags["-16 shr 2"])
        assertEquals(15, flags["-16 ushr 28"])
        assertEquals(-1, flags["0.inv()"])
    }

    @Test
    fun infixBindsLooserThanArithmetic() {
        assertEquals(8, 1 shl 2 + 1)
        assertEquals("a" to 3, "a" to 1 + 2)
    }
}
