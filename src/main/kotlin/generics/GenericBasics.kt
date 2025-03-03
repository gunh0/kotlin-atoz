package atoz.generics

// Type parameters go before the function name, `fun <T> f(x: T)`, and after
// the class name, `class Box<T>`. Type arguments are usually inferred, so you
// rarely write `Box<String>("x")`. There are no raw types: a List always has
// an element type.
//
// An unconstrained T has the upper bound Any?, so T itself may be nullable:
// firstOrDefault(listOf<String?>(null), "x") returns null with T = String?.
// Declare `<T : Any>` to rule that out, or use the definitely non-null type
// `T & Any` for one position only, e.g. a parameter that must not be null.

class BoundedStack<T>(private val capacity: Int) {
    private val items = ArrayList<T>()

    val size: Int get() = items.size

    fun push(item: T): Boolean {
        if (items.size == capacity) return false
        items += item
        return true
    }

    fun pop(): T? = items.removeLastOrNull()
    fun peek(): T? = items.lastOrNull()
}

fun <T> firstOrDefault(items: List<T>, default: T): T = if (items.isEmpty()) default else items[0]

fun <K, V> invert(map: Map<K, V>): Map<V, List<K>> = map.entries.groupBy({ it.value }, { it.key })

fun <T> List<T>.middle(): T? = if (isEmpty()) null else this[size / 2]

fun <T> withFallback(value: T, fallback: T & Any): T & Any = value ?: fallback

fun main() {
    val stack = BoundedStack<String>(2)
    println("push a: ${stack.push("a")}, push b: ${stack.push("b")}, push c: ${stack.push("c")}")
    println("pop: ${stack.pop()}, peek: ${stack.peek()}, size: ${stack.size}")
    println("firstOrDefault(empty, 42) = ${firstOrDefault(emptyList(), 42)}")
    println("firstOrDefault(listOf(null), \"x\") = ${firstOrDefault(listOf(null), "x")}")
    println("invert: ${invert(mapOf("alice" to "admin", "bob" to "viewer", "carol" to "admin"))}")
    println("middle: ${listOf(1, 2, 3, 4, 5).middle()}")
    println("withFallback(null, \"guest\") = ${withFallback(null as String?, "guest")}")
}
