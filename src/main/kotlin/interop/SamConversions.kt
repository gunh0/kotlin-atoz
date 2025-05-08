package atoz.interop

import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.function.BiFunction
import java.util.function.Predicate

/*
SAM conversions: a Kotlin lambda can be passed wherever Java expects an
interface with a single abstract method (Runnable, Callable, Comparator,
java.util.function.*). Default methods don't count, so you can still call
`andThen`, `negate`, `reversed` on the result.
- Passing a lambda to a Java method converts it automatically:
  `list.removeIf { it.isBlank() }`, `map.computeIfAbsent(key) { ... }`.
- To create an instance explicitly, use the SAM constructor:
  `Comparator<String> { a, b -> ... }`, `Runnable { ... }`.
- For Kotlin interfaces this only works when they are declared `fun interface`.
*/

fun interface Sanitizer {
    fun clean(input: String): String
}

val trimSanitizer = Sanitizer { it.trim() }

val byLengthThenAlphabet: Comparator<String> =
    Comparator<String> { a, b -> a.length - b.length }.thenComparing(Comparator.naturalOrder())

val notBlank: Predicate<String> = Predicate<String> { it.isBlank() }.negate()

fun removeBlank(items: MutableList<String>): MutableList<String> = items.apply { removeIf { it.isBlank() } }

fun groupByInitial(words: List<String>): Map<Char, MutableList<String>> {
    val groups = java.util.TreeMap<Char, MutableList<String>>()
    for (w in words) groups.computeIfAbsent(w.first()) { mutableListOf() }.add(w)
    return groups
}

fun runOnExecutor(tasks: List<Callable<String>>): List<String> {
    val executor = Executors.newFixedThreadPool(2)
    try {
        return executor.invokeAll(tasks).map { it.get() }
    } finally {
        executor.shutdown()
    }
}

val joinWith: BiFunction<String, String, String> = BiFunction { a, b -> "$a-$b" }

fun main() {
    println(listOf("kotlin", "go", "java", "c").sortedWith(byLengthThenAlphabet))
    println(removeBlank(mutableListOf("a", " ", "b", "")))
    println(groupByInitial(listOf("apple", "avocado", "banana")))
    println(runOnExecutor(listOf(Callable { "first" }, Callable { "second" })))
    println(joinWith.andThen { it.uppercase() }.apply("kotlin", "java"))
    println("${trimSanitizer.clean("  padded  ")}|${notBlank.test("   ")}")
}
