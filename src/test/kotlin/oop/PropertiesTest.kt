package atoz.oop

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PropertiesTest {
    @Test
    fun setterValidatesThroughBackingField() {
        val t = Thermostat(50)
        assertEquals(30, t.target)
        t.target = 5
        assertEquals(10, t.target)
        assertEquals(1, t.changes)
        assertTrue(t.isEco)
        t.target = 22
        assertFalse(t.isEco)
    }

    @Test
    fun lateinitThrowsWhenReadTooEarly() {
        val session = AuthSession()
        assertFalse(session.isAuthenticated)
        assertFailsWith<UninitializedPropertyAccessException> { session.token }
        session.token = "abc"
        assertTrue(session.isAuthenticated)
    }

    @Test
    fun lazyComputesOnce() {
        val config = LazyConfig { "port=8080" }
        assertEquals(0, config.loads)
        repeat(3) { assertEquals("port=8080", config.content) }
        assertEquals(1, config.loads)
    }
}
