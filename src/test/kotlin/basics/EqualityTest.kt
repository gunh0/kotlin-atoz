package atoz.basics

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotSame
import kotlin.test.assertTrue

class EqualityTest {
    @Test
    fun doubleEqualsComparesContent() {
        val literal = "kotlin"
        val built = freshCopy("kotlin")
        assertTrue(literal == built)
        assertFalse(literal === built)
    }

    @Test
    fun classesWithoutEqualsCompareByIdentity() {
        assertNotEquals(SessionToken("t"), SessionToken("t"))
        assertEquals(UserKey("acme", 1), UserKey("acme", 1))
        assertNotSame(UserKey("acme", 1), UserKey("acme", 1))
    }

    @Test
    fun equalityIsNullSafe() {
        val nothing: String? = null
        assertFalse(nothing == "x")
        assertTrue(nothing == null)
    }

    @Test
    fun floatingPointDependsOnStaticType() {
        assertTrue(ieeeEquals(-0.0, 0.0))
        assertFalse(boxedEquals(-0.0, 0.0))
        assertFalse(ieeeEquals(Double.NaN, Double.NaN))
        assertTrue(boxedEquals(Double.NaN, Double.NaN))
        assertTrue(listOf(Double.NaN) == listOf(Double.NaN))
    }
}
