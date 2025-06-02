package atoz.generics

// Generic types are invariant by default: a Box<String> is not a Box<Any>.
// Variance annotations on the declaration say how subtyping may flow:
// - `out T` (covariant): T only comes out (return types), so Producer<String>
//   is a Producer<Any>. Kotlin's read-only List<out E> is why a List<String>
//   can be passed where List<Any> is expected.
// - `in T` (contravariant): T only goes in (parameters), so Consumer<Any> is a
//   Consumer<String>, as with Comparable<in T>.
// Java expresses this at every use site with wildcards (? extends / ? super,
// "PECS"); Kotlin lets the class author state it once. MutableList<E> must
// stay invariant because it both reads and writes E.

interface Producer<out T> {
    fun produce(): T
}

interface Consumer<in T> {
    fun consume(item: T)
}

class ConstantProducer<out T>(private val value: T) : Producer<T> {
    override fun produce(): T = value
}

class CollectingConsumer<in T> : Consumer<T> {
    private val seen = mutableListOf<String>()
    override fun consume(item: T) {
        seen += item.toString()
    }

    fun seen(): List<String> = seen.toList()
}

fun describeAll(producers: List<Producer<Any>>): List<String> = producers.map { it.produce().toString() }

fun feedStrings(consumer: Consumer<String>, items: List<String>) = items.forEach(consumer::consume)

fun countAll(items: List<Any>): Int = items.size

fun main() {
    val strings: Producer<String> = ConstantProducer("token")
    val numbers: Producer<Int> = ConstantProducer(42)
    println(describeAll(listOf(strings, numbers))) // Producer<String> is a Producer<Any>

    val anyConsumer = CollectingConsumer<Any>()
    feedStrings(anyConsumer, listOf("a", "b")) // Consumer<Any> is a Consumer<String>
    println(anyConsumer.seen())

    val names: List<String> = listOf("x", "y")
    println(countAll(names)) // List<out E>
    // val mutable: MutableList<Any> = mutableListOf<String>() // does not compile
}
