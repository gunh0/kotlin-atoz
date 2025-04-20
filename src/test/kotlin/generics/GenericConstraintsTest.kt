package atoz.generics

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GenericConstraintsTest {
    @Test
    fun comparableBound() {
        assertEquals(10, clamp(15, 0, 10))
        assertEquals("f", clamp("m", "a", "f"))
        assertEquals(2.5, clamp(2.5, 0.0, 3.0))
        assertFailsWith<IllegalArgumentException> { clamp(1, 5, 0) }
        assertTrue(isSorted(listOf(1, 2, 2, 5)))
        assertFalse(isSorted(listOf("b", "a")))
    }

    @Test
    fun multipleBoundsWithWhere() {
        assertEquals("abe", shortestThenAlphabetical(listOf("delta", "bob", "abe", "zed")))
        assertNull(shortestThenAlphabetical(emptyList<String>()))
    }

    @Test
    fun nonNullBounds() {
        assertEquals(mapOf("a" to 1), compactMap(listOf("a" to 1, null to 2, "c" to null)))
    }

    @Test
    fun interfaceBound() {
        assertEquals(Policy("tls", 5), latest(listOf(Policy("tls", 3), Policy("tls", 5), Policy("tls", 4))))
    }
}
