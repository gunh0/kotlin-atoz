package atoz.collections

import kotlin.test.Test
import kotlin.test.assertEquals

class ReadOnlyVsMutableTest {
    @Test
    fun readOnlyViewObservesChanges() {
        val acl = AccessControlList()
        acl.grant("alice")
        val view = acl.view
        val snapshot = acl.snapshot
        acl.grant("bob")
        assertEquals(listOf("alice", "bob"), view)
        assertEquals(listOf("alice"), snapshot)
    }

    @Test
    fun readOnlyIsNotImmutable() {
        val acl = AccessControlList()
        (acl.view as MutableList<String>).add("mallory")
        assertEquals(listOf("mallory"), acl.snapshot)
    }

    @Test
    fun plusReturnsANewList() {
        val team = listOf("alice")
        assertEquals(listOf("alice", "auditor"), withAuditor(team))
        assertEquals(listOf("alice"), team)
    }
}
