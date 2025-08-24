package atoz.interop

import java.util.Collections

/*
Kotlin's read-only `List` is a compile-time view, not an immutable list.
On the JVM it is plain java.util.List, so Java code (or a cast) can call
`add`/`set` on it, and it succeeds whenever the object underneath is mutable.
- Exposing a private MutableList as `List` hands out a live, writable view.
- `toList()` makes a copy: callers may still mutate their copy, but not yours.
- For a list that is truly unmodifiable for Java callers, wrap the copy with
  `Collections.unmodifiableList`, or use Java's `List.copyOf`.
- `listOf(a, b)` is backed by Arrays.asList: `set` works from Java, `add` throws.
*/

class AccessList {
    private val members = mutableListOf("alice", "bob")

    val liveView: List<String> get() = members
    val copy: List<String> get() = members.toList()
    val unmodifiableCopy: List<String> get() = Collections.unmodifiableList(members.toList())

    fun contains(user: String) = user in members
}

// What a Java method receiving java.util.List<String> can do; the cast is all it takes from Kotlin.
fun javaStyleAdd(list: List<String>, element: String): Result<Unit> = runCatching {
    (list as MutableList<String>).add(element)
    Unit
}

fun main() {
    val acl = AccessList()
    println("add via live view: ${javaStyleAdd(acl.liveView, "mallory").isSuccess}, contains mallory: ${acl.contains("mallory")}")
    val fresh = AccessList()
    println("add via copy: ${javaStyleAdd(fresh.copy, "mallory").isSuccess}, contains mallory: ${fresh.contains("mallory")}")
    println("add via unmodifiable copy: ${javaStyleAdd(fresh.unmodifiableCopy, "mallory")}")
    println("listOf(...) is ${listOf("a", "b").javaClass.name}")
}
