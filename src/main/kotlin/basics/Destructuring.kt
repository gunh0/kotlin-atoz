package atoz.basics

// A destructuring declaration `val (a, b) = x` calls x.component1(),
// x.component2(), ... Data classes, Pair, Triple, Map.Entry and List (first
// five) provide them; any class can by declaring `operator fun componentN()`.
// It works in for loops (`for ((k, v) in map)`) and lambda parameters
// (`{ (k, v) -> ... }`), and `_` skips a component.
//
// Destructuring is positional, not by name. `val (max, min) = range` compiles
// happily and gives you the swapped values when the types match, so reorder
// constructor parameters of a data class with care.

data class Bounds(val min: Int, val max: Int)

class HostPort(private val raw: String) {
    operator fun component1(): String = raw.substringBefore(':')
    operator fun component2(): Int = raw.substringAfter(':').toInt()
}

fun widthOf(bounds: Bounds): Int {
    val (low, high) = bounds
    return high - low
}

fun swappedByMistake(bounds: Bounds): Pair<Int, Int> {
    val (max, min) = bounds // names don't matter: max gets component1() == bounds.min
    return max to min
}

fun portOf(address: String): Int {
    val (_, port) = HostPort(address)
    return port
}

fun describeCounts(counts: Map<String, Int>): List<String> =
    counts.map { (name, count) -> "$name=$count" }

fun minAndMax(values: List<Int>): Pair<Int, Int> = values.min() to values.max()

fun main() {
    val (min, max) = minAndMax(listOf(7, 2, 9))
    println("min=$min max=$max")
    println("width of 3..10 = ${widthOf(Bounds(3, 10))}")
    println("swapped: ${swappedByMistake(Bounds(3, 10))}")
    val (host, port) = HostPort("db.internal:5432")
    println("host=$host port=$port, portOf = ${portOf("cache:6379")}")
    for ((name, count) in mapOf("a" to 1, "b" to 2)) println("$name -> $count")
    println(describeCounts(mapOf("x" to 3)))
    val (first, second, _, fourth) = listOf("w", "x", "y", "z")
    println("$first $second $fourth")
}
