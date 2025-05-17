package atoz.oop

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertTrue

class DataClassesTest {
    @Test
    fun equalityUsesConstructorPropertiesOnly() {
        val a = Coordinates(1.0, 2.0).apply { label = "a" }
        val b = Coordinates(1.0, 2.0).apply { label = "b" }
        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
        assertEquals("Coordinates(lat=1.0, lon=2.0)", a.toString())
    }

    @Test
    fun copyIsShallow() {
        val release = Release("kotlin", "2.1.0", mutableListOf("stable"))
        val next = release.copy(version = "2.1.10")
        assertEquals(Release("kotlin", "2.1.10", listOf("stable")), next)
        assertNotSame(release, next)
        assertSame(release.tags, next.tags)
    }

    @Test
    fun componentFunctions() {
        val (name, version) = Release("kotlin", "2.1.0")
        assertEquals("kotlin", name)
        assertEquals("2.1.0", version)
    }

    @Test
    fun generatedToStringLeaksSecrets() {
        assertTrue("hunter2" in LoginRequest("ian", "hunter2").toString())
        assertFalse("hunter2" in SafeLoginRequest("ian", "hunter2").toString())
    }
}
