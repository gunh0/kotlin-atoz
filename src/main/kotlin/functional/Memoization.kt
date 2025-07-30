package atoz.functional

// Memoization caches a pure function's results by argument, trading memory
// for time. A HashMap plus getOrPut is enough for a single thread; wrapping it
// in a higher-order function memoizes any (A) -> R without touching its code.
//
// Recursive memoization has a JVM trap: Java's HashMap.computeIfAbsent throws
// ConcurrentModificationException (Java 9+) when the mapping function itself
// inserts into the map, which a recursive fib does. Kotlin's getOrPut is a
// plain get, compute, put sequence, so recursion works. Neither is
// thread-safe; use ConcurrentHashMap for shared caches, and bound the size when
// the arguments come from untrusted input (an unbounded cache is a DoS vector).

fun <A, R> memoize(f: (A) -> R): (A) -> R {
    val cache = HashMap<A, R>()
    return { a -> cache.getOrPut(a) { f(a) } }
}

class Fibonacci {
    private val cache = HashMap<Int, Long>()
    var computations = 0
        private set

    fun of(n: Int): Long = cache.getOrPut(n) {
        computations++
        if (n < 2) n.toLong() else of(n - 1) + of(n - 2)
    }
}

fun fibWithComputeIfAbsent(n: Int, cache: MutableMap<Int, Long> = HashMap()): Long =
    cache.computeIfAbsent(n) { if (it < 2) it.toLong() else fibWithComputeIfAbsent(it - 1, cache) + fibWithComputeIfAbsent(it - 2, cache) }

fun main() {
    var calls = 0
    val slowSquare: (Int) -> Int = { calls++; it * it }
    val fastSquare = memoize(slowSquare)
    repeat(3) { fastSquare(12) }
    println("fastSquare(12) = ${fastSquare(12)}, underlying calls = $calls")

    val fib = Fibonacci()
    println("fib(90) = ${fib.of(90)} with ${fib.computations} computations")
    println("computeIfAbsent version -> ${runCatching { fibWithComputeIfAbsent(20) }.exceptionOrNull()}")
}
