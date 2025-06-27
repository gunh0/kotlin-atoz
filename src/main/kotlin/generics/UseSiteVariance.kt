package atoz.generics

// When a type is invariant (MutableList, Array, your own class that both reads
// and writes T), you can still restrict it at the use site with a type
// projection, Kotlin's version of Java wildcards:
// - `MutableList<out Number>` (? extends Number): read as Number, no adds
// - `MutableList<in Int>` (? super Int): add Ints, reads give Any?
// - `List<*>` / `MutableList<*>` (unbounded ?): element type unknown; reads
//   give Any?, writes are forbidden
// copyInto below is the classic producer/consumer pair: it copies an
// Array<Int> into a MutableList<Number> or a MutableList<Any>, which plain
// invariant parameters (Array<T>, MutableList<T>) would refuse.

fun <T> copyInto(source: Array<out T>, target: MutableList<in T>): Int {
    source.forEach { target.add(it) }
    return source.size
}

fun sumAll(numbers: MutableList<out Number>): Double = numbers.sumOf { it.toDouble() }

fun addDefaults(target: MutableList<in Int>) {
    target.add(80)
    target.add(443)
}

fun describeUnknown(items: List<*>): String = items.joinToString { it?.let { v -> v::class.simpleName } ?: "null" }

fun clearAny(items: MutableList<*>): Int {
    val removed = items.size
    items.clear() // allowed: does not need the element type
    return removed
}

fun main() {
    val numbers: MutableList<Number> = mutableListOf(1.5)
    copyInto(arrayOf(1, 2), numbers)
    println("numbers after copy: $numbers, sum ${sumAll(numbers)}")

    val anything: MutableList<Any> = mutableListOf("start")
    addDefaults(anything)
    println("anything: $anything")

    println("describeUnknown: ${describeUnknown(listOf(1, "a", null, 2.0))}")
    println("clearAny removed ${clearAny(mutableListOf("x", "y"))}")
}
