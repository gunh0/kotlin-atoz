package atoz.modern

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ExplicitBackingFieldsTest {
    @Test
    fun mutatesThroughTheFieldTypeInside() {
        val tracker = LockoutTracker(maxFailures = 2)
        tracker.recordFailure("alice")
        assertEquals("open", tracker.status.value)
        tracker.recordFailure("alice")
        assertEquals(listOf("alice", "alice"), tracker.failures)
        assertEquals("locked", tracker.status.value)
        tracker.reset()
        assertEquals(emptyList(), tracker.failures)
        assertEquals("open", tracker.status.value)
    }

    @Test
    fun publicTypeIsTheReadOnlyOne() {
        val getter = LockoutTracker::class.java.getMethod("getFailures")
        assertEquals(List::class.java, getter.returnType)
        assertEquals(StateFlow::class.java, LockoutTracker::class.java.getMethod("getStatus").returnType)
        val status: StateFlow<String> = LockoutTracker(1).status
        assertEquals("open", status.value)
    }

    @Test
    fun itNarrowsTheTypeNotTheObject() {
        val tracker = LockoutTracker(maxFailures = 5)
        val leaked = tracker.failures
        (leaked as MutableList<String>).add("mallory") // a cast still reaches the mutable list
        assertEquals(listOf("mallory"), tracker.failures)
        assertTrue(tracker.status is MutableStateFlow<String>)
        assertSame(leaked, tracker.failures)
    }

    @Test
    fun sameBehaviourAsTheBackingPropertyIdiom() {
        val legacy = LegacyLockoutTracker()
        legacy.recordFailure("bob")
        assertEquals(listOf("bob"), legacy.failures)
    }
}
