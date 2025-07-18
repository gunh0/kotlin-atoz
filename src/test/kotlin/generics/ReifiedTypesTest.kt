package atoz.generics

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class ReifiedTypesTest {
    private val mixed = listOf(1, "two", 3.0, "four", null)

    @Test
    fun isTWorksWithReified() {
        assertEquals(listOf("two", "four"), mixed.ofType<String>())
        assertEquals(listOf(1), mixed.ofType<Int>())
        assertEquals(42, 42.castOrNull<Int>())
        assertNull("x".castOrNull<Int>())
    }

    @Test
    fun classOfT() {
        assertEquals("String", simpleNameOf<String>())
        assertEquals("Map", simpleNameOf<Map<String, Int>>())
    }

    @Test
    fun enumValuesOfT() {
        assertEquals(Algorithm.SHA512, parseEnumOrNull<Algorithm>(" sha512 "))
        assertNull(parseEnumOrNull<Algorithm>("md5"))
    }

    @Test
    fun typedSettingLookup() {
        val settings = mapOf("port" to 8080, "host" to "localhost")
        assertEquals(8080, settingOf<Int>(settings, "port"))
        assertEquals("localhost", settingOf<String>(settings, "host"))
        val e = assertFailsWith<IllegalArgumentException> { settingOf<Int>(settings, "host") }
        assertEquals("setting host is String, expected Int", e.message)
        assertFailsWith<NoSuchElementException> { settingOf<Int>(settings, "tls") }
    }
}
