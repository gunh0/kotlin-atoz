package atoz.collections

// List operations are eager: each step processes the whole list and builds a
// new one before the next step starts. A Sequence is lazy, like a Java Stream:
// intermediate operations only describe the pipeline, a terminal operation
// (toList, first, sum, ...) pulls elements through it one at a time, and
// short-circuiting operations (first, take, any) stop pulling early.
//
// Sequences pay off for long pipelines, large or infinite sources, and early
// exits; for a few small steps over a small list, eager lists are as fast and
// simpler. generateSequence(seed) { next } and sequence { yield(x) } build
// (possibly infinite) sources. Unlike a Stream, a Sequence can usually be
// iterated more than once.

fun eagerTrace(items: List<Int>, log: MutableList<String>): Int =
    items.map { log += "map $it"; it * 10 }
        .filter { log += "filter $it"; it > 15 }
        .first()

fun lazyTrace(items: List<Int>, log: MutableList<String>): Int =
    items.asSequence()
        .map { log += "map $it"; it * 10 }
        .filter { log += "filter $it"; it > 15 }
        .first()

fun powersOfTwo(): Sequence<Long> = generateSequence(1L) { it * 2 }

fun fibonacci(): Sequence<Long> = sequence {
    var a = 0L
    var b = 1L
    while (true) {
        yield(a)
        val next = a + b
        a = b
        b = next
    }
}

fun backoffDelays(baseMillis: Long, maxMillis: Long): List<Long> =
    powersOfTwo().map { it * baseMillis }.takeWhile { it <= maxMillis }.toList()

fun main() {
    val eagerLog = mutableListOf<String>()
    eagerTrace(listOf(1, 2, 3), eagerLog)
    println("eager: $eagerLog")
    val lazyLog = mutableListOf<String>()
    lazyTrace(listOf(1, 2, 3), lazyLog)
    println("lazy:  $lazyLog")
    println("first 10 fibonacci: ${fibonacci().take(10).toList()}")
    println("backoff: ${backoffDelays(100, 5_000)}")
}
