package atoz.collections

import kotlin.test.Test
import kotlin.test.assertEquals

class CollectionBuildersTest {
    @Test
    fun setOfAndMapOfKeepInsertionOrder() {
        assertEquals(listOf("b", "a", "c"), setOf("b", "a", "b", "c").toList())
        assertEquals(listOf("z", "a"), mapOf("z" to 1, "a" to 2).keys.toList())
    }

    @Test
    fun buildersAddConditionally() {
        assertEquals(mapOf("User-Agent" to "atoz/1.0"), defaultHeaders(null, json = false))
        assertEquals(
            listOf("User-Agent", "Authorization", "Accept", "Content-Type"),
            defaultHeaders("t", json = true).keys.toList(),
        )
        assertEquals(listOf("grep", "-v", "a.log"), commandLine("grep", verbose = true, files = listOf("a.log")))
        assertEquals(listOf("grep"), commandLine("grep", verbose = false, files = emptyList()))
    }

    @Test
    fun otherFactories() {
        assertEquals(listOf(0, 1, 4, 9), squares(4))
        assertEquals(listOf("--trace"), optionalFlags(null, "--trace"))
        assertEquals(emptyList(), optionalFlags(null, null))
    }
}
