package atoz.oop

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ConstructorsTest {
    @Test
    fun defaultsMayDependOnEarlierParameters() {
        val default = Endpoint("example.com")
        assertEquals(443, default.port)
        assertTrue(default.secure)
        assertEquals("https://example.com:443", default.url())

        val local = Endpoint("localhost", 8080)
        assertFalse(local.secure)
        assertEquals("http://localhost:8080", local.url())
        assertEquals("https://internal:8443", Endpoint("internal", 8443, secure = true).url())
    }

    @Test
    fun secondaryConstructorDelegates() {
        assertEquals("http://pair.example:80", Endpoint("pair.example" to 80).url())
    }

    @Test
    fun plainParametersFeedInitializers() {
        assertEquals("Hello, Kotlin", Greeter("  Kotlin ").greet())
    }

    @Test
    fun secondaryOnlyClass() {
        assertEquals(0, LegacyCounter().start)
        assertEquals(5, LegacyCounter(5).start)
    }
}
