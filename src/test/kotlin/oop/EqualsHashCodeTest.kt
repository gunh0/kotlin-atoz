package atoz.oop

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class EqualsHashCodeTest {
    @Test
    fun withoutOverridesEqualityIsIdentity() {
        val a = IdentityHost("db", 5432)
        assertNotEquals(a, IdentityHost("db", 5432))
        assertEquals(2, setOf(a, IdentityHost("db", 5432)).size)
        assertEquals(a, a)
    }

    @Test
    fun handWrittenEqualsAndHashCodeAgree() {
        val a = HostKey("DB.internal", 5432)
        val b = HostKey("db.internal", 5432)
        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
        assertEquals(1, setOf(a, b).size)
        assertEquals("primary", mapOf(a to "primary")[b])
        assertFalse(a.equals(null))
        assertNotEquals(a, HostKey("db.internal", 5433))
    }

    @Test
    fun mutatingAKeyLosesItInsideTheSet() {
        val key = MutableHostKey("evil.example", 443)
        val blocked = hashSetOf(key)
        key.name = "renamed.example"
        assertFalse(key in blocked) // looked up in the bucket of the new hash
        assertFalse(blocked.remove(key))
        assertEquals(1, blocked.size) // still there, unreachable by lookup
        assertTrue(blocked.any { it === key }) // iteration still finds it
    }

    @Test
    fun openHierarchyBreaksSymmetry() {
        val p = OpenPoint(1, 2)
        val c = ColoredPoint(1, 2, "red")
        assertTrue(p == c)
        assertFalse(c == p)
    }
}
