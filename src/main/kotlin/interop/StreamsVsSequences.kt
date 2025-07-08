package atoz.interop

import java.util.stream.Collectors
import java.util.stream.Stream
import kotlin.streams.asSequence
import kotlin.streams.asStream

/*
Java Streams vs Kotlin collections and sequences:
- Kotlin's `map`/`filter` on a List are eager: each step builds a new list.
- `asSequence()` makes them lazy and element-by-element, like a Stream,
  so `first()` stops early and no intermediate lists are allocated.
- A Stream can be consumed only once; a Sequence over a collection can be
  iterated again (a sequence over a one-shot source can't, see `constrainOnce`).
- Converting is one call: `stream.asSequence()`, `sequence.asStream()`.
  `stream.toList()` is the JDK 16 member and returns an unmodifiable list.
Prefer sequences in Kotlin code; use streams where a Java API hands you one
or when you want `parallel()`.
*/

fun traceEager(numbers: List<Int>, log: MutableList<String>): Int =
    numbers.map { log += "map $it"; it * 10 }.first { log += "check $it"; it > 15 }

fun traceLazy(numbers: List<Int>, log: MutableList<String>): Int =
    numbers.asSequence().map { log += "map $it"; it * 10 }.first { log += "check $it"; it > 15 }

fun traceStream(numbers: List<Int>, log: MutableList<String>): Int =
    numbers.stream().map { log += "map $it"; it * 10 }.filter { log += "check $it"; it > 15 }.findFirst().get()

fun wordLengths(words: Stream<String>): Map<String, Int> =
    words.asSequence().filter { it.isNotBlank() }.associateWith { it.length }

fun javaStyleJoin(words: List<String>): String =
    words.stream().map(String::uppercase).collect(Collectors.joining(", ", "[", "]"))

fun main() {
    for ((name, f) in listOf("eager" to ::traceEager, "lazy" to ::traceLazy, "stream" to ::traceStream)) {
        val log = mutableListOf<String>()
        val result = f(listOf(1, 2, 3), log)
        println("$name -> $result $log")
    }
    println(wordLengths(Stream.of("kotlin", " ", "java")))
    println(javaStyleJoin(listOf("a", "b")))
    println(sequenceOf(3, 1, 2).sorted().asStream().toList())
}
