package atoz.generics

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GenericBasicsTest {
    @Test
    fun genericClass() {
        val stack = BoundedStack<Int>(2)
        assertTrue(stack.push(1))
        assertTrue(stack.push(2))
        assertFalse(stack.push(3))
        assertEquals(2, stack.pop())
        assertEquals(1, stack.peek())
        assertEquals(1, stack.size)
    }

    @Test
    fun inferredTypeArguments() {
        assertEquals(42, firstOrDefault(emptyList(), 42))
        assertEquals("a", firstOrDefault(listOf("a"), "x"))
        assertEquals(3, listOf(1, 2, 3, 4, 5).middle())
        assertNull(emptyList<String>().middle())
    }

    @Test
    fun unconstrainedTMayBeNullable() {
        assertNull(firstOrDefault(listOf<String?>(null), "x"))
        val name: String = withFallback(null as String?, "guest")
        assertEquals("guest", name)
    }

    @Test
    fun genericFunctionWithTwoParameters() {
        assertEquals(
            mapOf("admin" to listOf("alice", "carol"), "viewer" to listOf("bob")),
            invert(mapOf("alice" to "admin", "bob" to "viewer", "carol" to "admin")),
        )
    }
}
