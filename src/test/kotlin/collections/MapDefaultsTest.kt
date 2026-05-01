package atoz.collections

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull

class MapDefaultsTest {
    @Test
    fun withDefaultOnlyAffectsGetValue() {
        assertEquals(600, limitFor("admin"))
        assertEquals(60, limitFor("guest"))
        assertNull(requestsPerMinute["guest"])
        assertFalse("guest" in requestsPerMinute) // the default is not stored
        assertFailsWith<NoSuchElementException> { mapOf("a" to 1).getValue("b") }
    }

    @Test
    fun nullValuesAndGetOrElse() {
        val config = mapOf<String, String?>("proxy" to null)
        assertEquals("direct", config.getOrElse("proxy") { "direct" })
        assertNull(config.getOrDefault("proxy", "direct"))
        assertEquals("direct", config.getOrDefault("missing", "direct"))
    }

    @Test
    fun mapKeysDropsCollisionsSilently() {
        val headers = listOf("Host" to "api.example.com", "host" to "evil.example", "Accept" to "*/*")
        assertEquals(mapOf("host" to "evil.example", "accept" to "*/*"), lowercaseHeaders(headers))
        assertEquals(setOf("host"), duplicateHeaderNames(headers))
        assertEquals(emptySet(), duplicateHeaderNames(listOf("Host" to "a")))
    }

    @Test
    fun viewsWriteThroughButFiltersCopy() {
        val sessions = mutableMapOf("s1" to "alice", "s2" to "bob", "s3" to "alice")
        val bobOnly = sessions.filterValues { it == "bob" }
        sessions.values.removeAll { it == "alice" }
        assertEquals(mapOf("s2" to "bob"), sessions)
        sessions.keys.remove("s2")
        assertEquals(emptyMap(), sessions)
        assertEquals(mapOf("s2" to "bob"), bobOnly) // the copy is unaffected
    }

    @Test
    fun toSortedMapOrdersByKey() {
        assertEquals(listOf("a", "b", "c"), mapOf("c" to 3, "a" to 1, "b" to 2).toSortedMap().keys.toList())
    }
}
