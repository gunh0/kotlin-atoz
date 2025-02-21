package atoz.interop

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PlatformTypesTest {
    private val missing = "atoz.interop.missing"

    @Test
    fun nullableTargetForcesHandling() {
        assertNull(propertyOrNull(missing))
        assertEquals("fallback", propertyOrDefault(missing, "fallback"))
        assertEquals(System.getProperty("java.version"), propertyOrNull("java.version"))
    }

    @Test
    fun nonNullTargetFailsFastAtTheBoundary() {
        val e = assertFailsWith<NullPointerException> { requiredProperty(missing) }
        assertTrue(e.message!!.contains("getProperty(...)"), e.message)
    }

    @Test
    fun directCallsFailLikeJava() {
        assertFailsWith<NullPointerException> { propertyLength(missing) }
    }

    @Test
    fun javaApisReturnNullsAndOptionals() {
        val settings = loadSettings("timeout=30")
        assertEquals("30", settings.getProperty("timeout"))
        assertNull(settings.getProperty("retries"))
        assertEquals("suspend", firstLongWord(listOf("val", "suspend", "inline")))
        assertNull(firstLongWord(listOf("val", "var")))
    }
}
