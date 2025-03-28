package atoz.interop

/*
What Java sees of Kotlin declarations, and the annotations that change it:
- Members of an `object` or `companion object` are instance members of a
  singleton (`IdGenerator.INSTANCE.next()` from Java) unless marked
  @JvmStatic, which adds a real static method.
- A property compiles to a private field plus getter/setter. @JvmField
  exposes the field itself (`const val` does this for compile-time constants).
- Default arguments don't exist in Java; @JvmOverloads generates one
  overload per trailing default parameter.
- @JvmName renames the JVM method, e.g. to resolve two functions that differ
  only in generic arguments and would otherwise clash after type erasure.
*/

object IdGenerator {
    private var counter = 0

    const val PREFIX = "id-"

    @JvmField
    val separator = ":"

    val scope = "global" // getScope() on INSTANCE

    @JvmStatic
    fun next(): String = "$PREFIX${++counter}"

    fun reset() {
        counter = 0
    }
}

class Endpoint @JvmOverloads constructor(
    val host: String,
    val port: Int = 443,
    val tls: Boolean = true,
) {
    override fun toString() = "${if (tls) "https" else "http"}://$host:$port"
}

// Both erase to total(List): without @JvmName this is a "platform declaration clash".
fun total(values: List<Int>): Int = values.sum()

@JvmName("totalLengths")
fun total(values: List<String>): Int = values.sumOf { it.length }

fun main() {
    println("${IdGenerator.next()} ${IdGenerator.next()}")
    println(Endpoint("example.com"))
    println("ints: ${total(listOf(1, 2, 3))}, strings: ${total(listOf("ab", "cde"))}")
    val facade = Class.forName("atoz.interop.JvmAnnotationsKt")
    println("JVM names: ${facade.declaredMethods.map { it.name }.sorted()}")
}
