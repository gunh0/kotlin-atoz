package atoz.collections

import kotlin.test.Test
import kotlin.test.assertEquals

class GroupingTest {
    private val events = listOf(
        LoginEvent("alice", "10.0.0.1", true),
        LoginEvent("bob", "10.0.0.2", false),
        LoginEvent("bob", "10.0.0.3", false),
        LoginEvent("bob", "10.0.0.2", true),
        LoginEvent("carol", "10.0.0.4", false),
    )

    @Test
    fun groupingByEachCount() {
        assertEquals(mapOf("bob" to 2, "carol" to 1), failuresByUser(events))
    }

    @Test
    fun groupByKeepsEveryElement() {
        assertEquals(
            mapOf("alice" to listOf("10.0.0.1"), "bob" to listOf("10.0.0.2", "10.0.0.3", "10.0.0.2"), "carol" to listOf("10.0.0.4")),
            ipsByUser(events),
        )
        assertEquals(listOf("alice", "bob", "carol"), ipsByUser(events).keys.toList())
    }

    @Test
    fun associateByKeepsTheLastDuplicate() {
        val last = lastEventByUser(events)
        assertEquals(3, last.size)
        assertEquals(LoginEvent("bob", "10.0.0.2", true), last["bob"])
    }

    @Test
    fun associateWithAndPartition() {
        assertEquals(mapOf("alice" to 5, "bob" to 3), userNameLengths(listOf("alice", "bob")))
        val (ok, failed) = splitByOutcome(events)
        assertEquals(2, ok.size)
        assertEquals(3, failed.size)
    }
}
