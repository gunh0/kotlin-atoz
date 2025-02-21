package atoz.interop

import java.util.Optional
import java.util.Properties
import kotlin.jvm.optionals.getOrNull

/*
Platform types: Java declarations carry no nullability, so Kotlin types their
results as `String!` ("String or String?, you decide"). You can't write that
type yourself; it only appears in IDE hints and error messages.
- Assign it to `String?` and the compiler makes you handle null.
- Assign it to `String` and Kotlin inserts a null check right there: a null
  fails fast with an NPE naming the Java call, instead of surfacing later.
- Call a method on it directly and you get Java's plain NPE behaviour.
Java code annotated with @Nullable/@NotNull (JSpecify, JetBrains, JSR-305...)
gets real Kotlin types instead of platform types.
*/

// System.getProperty is declared in Java as `String getProperty(String)`: the result is `String!`.
fun propertyOrNull(name: String): String? = System.getProperty(name)

fun propertyOrDefault(name: String, default: String): String = System.getProperty(name) ?: default

fun requiredProperty(name: String): String {
    val value: String = System.getProperty(name) // compiler-inserted null check happens here
    return value
}

fun propertyLength(name: String): Int = System.getProperty(name).length // unchecked: plain NPE

fun loadSettings(text: String): Properties = Properties().apply { load(text.reader()) }

// Optional from Java APIs: getOrNull() turns it back into a Kotlin nullable type.
fun firstLongWord(words: List<String>): String? = words.stream().filter { it.length > 5 }.findFirst().getOrNull()

fun main() {
    println("java.version = ${propertyOrNull("java.version")}")
    println("missing = ${propertyOrNull("atoz.missing")}")
    println("with default = ${propertyOrDefault("atoz.missing", "fallback")}")
    println(runCatching { requiredProperty("atoz.missing") })
    val settings = loadSettings("timeout=30")
    println("timeout=${settings.getProperty("timeout")}, retries=${settings.getProperty("retries")}")
    println(firstLongWord(listOf("val", "suspend", "inline")))
}
