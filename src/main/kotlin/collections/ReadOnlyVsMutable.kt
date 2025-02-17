package atoz.collections

// Kotlin splits each collection interface in two: List/Set/Map only read,
// MutableList/MutableSet/MutableMap add the writing methods. On the JVM both
// map to the same java.util interfaces, so it is a compile-time view, not a
// guarantee: a read-only List is not an immutable list.
// - A `List` property backed by a MutableList changes when the owner mutates it.
// - A caller can cast it back to MutableList and modify the owner's state.
// Return a copy (toList()) when callers must not observe or cause changes.
//
// `val` vs read-only are separate axes: a `val` MutableList can still change,
// a `var` List can be reassigned. `+` on a read-only list returns a new list.

class AccessControlList {
    private val members = mutableListOf<String>()

    val view: List<String> get() = members // read-only type, same object
    val snapshot: List<String> get() = members.toList() // defensive copy

    fun grant(user: String) {
        members += user
    }
}

fun withAuditor(team: List<String>): List<String> = team + "auditor"

fun main() {
    val acl = AccessControlList()
    acl.grant("alice")
    val view = acl.view
    val snapshot = acl.snapshot
    acl.grant("bob")
    println("view sees later changes: $view, snapshot does not: $snapshot")

    (acl.view as MutableList<String>).add("mallory")
    println("after a cast through the view: ${acl.snapshot}")

    val team = listOf("alice")
    println("team + auditor = ${withAuditor(team)}, team itself = $team")
}
