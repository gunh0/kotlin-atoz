package atoz.collections

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AggregationTest {
    private val requests = listOf(
        Request("/", 12, 512),
        Request("/search", 340, 20_480),
        Request("/api/v1/users", 95, Int.MAX_VALUE),
    )

    @Test
    fun countAndSumOf() {
        assertEquals(1, slowRequests(requests, 100))
        assertEquals(512L + 20_480 + Int.MAX_VALUE, totalBytes(requests))
    }

    @Test
    fun averageAndExtremes() {
        assertEquals(149.0, averageMillis(requests))
        assertEquals("/search", slowest(requests)?.path)
        assertEquals(13, longestPathLength(requests))
    }

    @Test
    fun emptyCollections() {
        assertTrue(averageMillis(emptyList()).isNaN())
        assertNull(slowest(emptyList()))
        assertFailsWith<NoSuchElementException> { longestPathLength(emptyList()) }
        assertTrue(allFast(emptyList(), 0))
        assertEquals(0, emptyList<Request>().sumOf { it.bytes })
    }
}
