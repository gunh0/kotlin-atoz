package atoz.modern

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/*
Explicit backing fields (stable in 2.4): a property can declare its field with
a more specific type than the property itself:
    val failures: List<String>
        field = mutableListOf()
Outside the class the property is a read-only List; inside, the compiler
smart-casts it to the field's MutableList. This replaces the "backing
property" idiom of a private `_failures` plus a public getter (and Java's
private field + getter returning the interface type).
It narrows the *type*, not the object: callers get the same instance, so a
cast to MutableList still works. Return a copy when that matters.
*/

class LockoutTracker(private val maxFailures: Int) {
    val failures: List<String>
        field = mutableListOf()

    val status: StateFlow<String>
        field = MutableStateFlow("open")

    fun recordFailure(user: String) {
        failures.add(user) // smart cast to MutableList inside the class
        if (failures.size >= maxFailures) status.value = "locked"
    }

    fun reset() {
        failures.clear()
        status.value = "open"
    }
}

// The pre-2.4 idiom, for comparison.
class LegacyLockoutTracker {
    private val _failures = mutableListOf<String>()
    val failures: List<String> get() = _failures

    fun recordFailure(user: String) {
        _failures += user
    }
}

fun main() {
    val tracker = LockoutTracker(maxFailures = 3)
    repeat(3) { tracker.recordFailure("alice") }
    println("failures=${tracker.failures} status=${tracker.status.value}")
    tracker.reset()
    println("after reset: failures=${tracker.failures} status=${tracker.status.value}")
}
