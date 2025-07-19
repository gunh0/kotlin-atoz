package atoz.basics

import kotlin.test.Test
import kotlin.test.assertEquals

class DestructuringTest {
    @Test
    fun dataClassesAndPairs() {
        assertEquals(7, widthOf(Bounds(3, 10)))
        val (min, max) = minAndMax(listOf(7, 2, 9))
        assertEquals(2, min)
        assertEquals(9, max)
    }

    @Test
    fun destructuringIsPositional() {
        assertEquals(3 to 10, swappedByMistake(Bounds(3, 10)))
    }

    @Test
    fun customComponentFunctions() {
        val (host, port) = HostPort("db.internal:5432")
        assertEquals("db.internal", host)
        assertEquals(5432, port)
        assertEquals(6379, portOf("cache:6379"))
    }

    @Test
    fun mapEntriesInLambdas() {
        assertEquals(listOf("a=1", "b=2"), describeCounts(mapOf("a" to 1, "b" to 2)))
    }
}
