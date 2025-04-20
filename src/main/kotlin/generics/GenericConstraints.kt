package atoz.generics

// An upper bound restricts a type parameter: `<T : Comparable<T>>` accepts only
// types that can compare themselves, and inside the function T has all the
// members of its bound (Java: `<T extends Comparable<T>>`). The default bound
// is Any?; `<T : Any>` excludes nullable types.
//
// Several bounds go into a `where` clause, since the angle brackets allow only
// one: `where T : CharSequence, T : Comparable<T>` (Java: `T extends A & B`).
// Bounds are checked at compile time, so a call with an unsuitable type does
// not compile, rather than failing with a ClassCastException at runtime.

fun <T : Comparable<T>> clamp(value: T, min: T, max: T): T {
    require(min <= max) { "empty range: $min > $max" }
    return when {
        value < min -> min
        value > max -> max
        else -> value
    }
}

fun <T : Comparable<T>> isSorted(items: List<T>): Boolean = items.zipWithNext().all { (a, b) -> a <= b }

fun <T> shortestThenAlphabetical(items: List<T>): T? where T : CharSequence, T : Comparable<T> =
    items.minWithOrNull(compareBy<T> { it.length }.thenBy { it })

fun <K : Any, V : Any> compactMap(entries: List<Pair<K?, V?>>): Map<K, V> =
    entries.mapNotNull { (k, v) -> if (k != null && v != null) k to v else null }.toMap()

interface Versioned {
    val version: Int
}

data class Policy(val name: String, override val version: Int) : Versioned

fun <T : Versioned> latest(items: List<T>): T? = items.maxByOrNull { it.version }

fun main() {
    println("clamp(15, 0, 10) = ${clamp(15, 0, 10)}, clamp(\"m\", \"a\", \"f\") = ${clamp("m", "a", "f")}")
    println("isSorted: ${isSorted(listOf(1, 2, 2, 5))} / ${isSorted(listOf("b", "a"))}")
    println("shortestThenAlphabetical: ${shortestThenAlphabetical(listOf("delta", "bob", "abe", "zed"))}")
    println("compactMap: ${compactMap(listOf("a" to 1, null to 2, "c" to null))}")
    println("latest policy: ${latest(listOf(Policy("tls", 3), Policy("tls", 5), Policy("tls", 4)))}")
}
