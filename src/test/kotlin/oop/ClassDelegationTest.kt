package atoz.oop

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ClassDelegationTest {
    @Test
    fun countsEveryAttemptExactlyOnce() {
        val set = CountingSet<String>()
        set.add("a")
        set.addAll(listOf("a", "b", "c"))
        assertEquals(3, set.size)
        assertEquals(4, set.attempted)
        assertTrue("b" in set)
    }

    @Test
    fun forwardedMembersNeedNoCode() {
        val set = CountingSet(linkedSetOf("x", "y"))
        set.remove("x")
        assertEquals(listOf("y"), set.toList())
    }

    @Test
    fun overrideOnlyWhatChanges() {
        val primary = InMemoryRepository().apply { save(1, "alice") }
        val replica = ReadOnlyGuard(primary)
        assertEquals("alice", replica.find(1))
        assertFailsWith<UnsupportedOperationException> { replica.save(2, "bob") }
        assertNull(primary.find(2))
    }
}
