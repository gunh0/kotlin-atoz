package atoz.basics

// Unit is Kotlin's `void`, except that it is a real type with a single value
// (the object `Unit`). A function without a declared return type returns it,
// and because it is a type, `() -> Unit` or `List<Unit>` just work; Java needs
// the boxed `Void` and `return null` for the same thing.
//
// Nothing is the type with no values: a function returning Nothing never
// returns normally (throw, TODO(), error(), an infinite loop). Nothing is a
// subtype of every type, so `map[key] ?: failWith("...")` still has the type
// of the map's values. `null` alone has type Nothing?, and emptyList() can be
// a List<Nothing> that fits any List<T> because List is covariant.

fun appendLine(sink: MutableList<String>, line: String) {
    sink += line
}

fun failWith(message: String): Nothing = throw IllegalStateException(message)

fun lookupPort(services: Map<String, Int>, name: String): Int =
    services[name] ?: failWith("unknown service: $name")

fun notYetImplemented(): Int = TODO("decide the retry policy")

val noRoles: List<Nothing> = emptyList()
fun rolesOf(admin: Boolean): List<String> = if (admin) listOf("admin") else noRoles

fun main() {
    val sink = mutableListOf<String>()
    val result: Unit = appendLine(sink, "hello")
    println("appendLine returned $result, sink = $sink")
    println("ssh port = ${lookupPort(mapOf("ssh" to 22), "ssh")}")
    println("lookup ftp -> ${runCatching { lookupPort(mapOf("ssh" to 22), "ftp") }.exceptionOrNull()}")
    println("TODO() -> ${runCatching { notYetImplemented() }.exceptionOrNull()}")
    println("roles: ${rolesOf(true)} / ${rolesOf(false)}")
}
