package atoz.oop

// `object Name { ... }` declares a class and its single instance at once: a
// singleton, initialized lazily and thread-safely on first access (the JVM's
// class initialization, like Java's holder idiom).
//
// Kotlin has no `static`. Members shared by all instances go into a
// `companion object`, called through the class name: constants, and factory
// functions that can validate input and return a cached or null result where a
// constructor could not. A companion is a real object, so it can implement
// interfaces. An object expression `object : Comparator<T> { ... }` creates an
// anonymous instance, Kotlin's version of an anonymous inner class.

object SecurityDefaults {
    const val SESSION_MINUTES = 30
    val allowedSchemes: Set<String> = setOf("https", "wss")

    fun isAllowed(url: String): Boolean = url.substringBefore("://") in allowedSchemes
}

interface ParserOf<T> {
    fun parse(raw: String): T?
}

class Temperature private constructor(val kelvin: Double) {
    val celsius: Double get() = kelvin - 273.15

    override fun toString(): String = "%.2f K".format(kelvin)

    companion object : ParserOf<Temperature> {
        const val ABSOLUTE_ZERO = 0.0

        fun ofKelvin(k: Double): Temperature {
            require(k >= ABSOLUTE_ZERO) { "below absolute zero: $k" }
            return Temperature(k)
        }

        fun ofCelsius(c: Double): Temperature = ofKelvin(c + 273.15)

        override fun parse(raw: String): Temperature? =
            raw.removeSuffix("C").toDoubleOrNull()?.takeIf { it >= -273.15 }?.let(::ofCelsius)
    }
}

val byLengthThenAlpha: Comparator<String> = object : Comparator<String> {
    override fun compare(a: String, b: String): Int =
        if (a.length != b.length) a.length - b.length else a.compareTo(b)
}

fun <T> parseAll(parser: ParserOf<T>, inputs: List<String>): List<T> = inputs.mapNotNull(parser::parse)

fun main() {
    println("session: ${SecurityDefaults.SESSION_MINUTES} min, https allowed: ${SecurityDefaults.isAllowed("https://a")}")
    println("ftp allowed: ${SecurityDefaults.isAllowed("ftp://a")}")
    println("ofCelsius(25.0) = ${Temperature.ofCelsius(25.0)}")
    println("ofKelvin(-1) -> ${runCatching { Temperature.ofKelvin(-1.0) }.exceptionOrNull()}")
    println("parse via the companion as an interface: ${parseAll(Temperature, listOf("21.5C", "hot", "-300C"))}")
    println(listOf("ccc", "a", "bb", "aa").sortedWith(byLengthThenAlpha))
}
