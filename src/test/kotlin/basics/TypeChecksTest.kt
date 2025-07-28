package atoz.basics

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TypeChecksTest {
    @Test
    fun isSmartCasts() {
        assertEquals("string of length 3", describeValue("abc"))
        assertEquals("int, doubled 42", describeValue(21))
        assertEquals("list of 2", describeValue(listOf(1, 2)))
        assertEquals("something else: Double", describeValue(3.5))
        assertEquals("null", describeValue(null))
        assertTrue(isNonBlankString("x"))
        assertFalse(isNonBlankString("  "))
        assertFalse(isNonBlankString(1))
    }

    @Test
    fun safeCastYieldsNull() {
        assertEquals(3, lengthIfString("abc"))
        assertNull(lengthIfString(42))
        assertEquals(8080, portFrom(8080))
        assertEquals(443, portFrom("443"))
        assertEquals(-1, portFrom(1.5))
    }

    @Test
    fun unsafeCastThrows() {
        assertEquals("ok", forceString("ok"))
        assertFailsWith<ClassCastException> { forceString(42) }
        assertFailsWith<NullPointerException> { forceString(null) }
    }
}
