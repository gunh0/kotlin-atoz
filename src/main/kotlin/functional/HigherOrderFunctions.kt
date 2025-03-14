package atoz.functional

// A higher-order function takes functions as parameters or returns one.
// The standard library is built on them (map, filter, use, synchronized, ...),
// and writing your own is how Kotlin removes boilerplate around a block of
// code: retries, timing, transactions, locking.
//
// When the last parameter is a function, the lambda goes outside the
// parentheses (trailing lambda syntax), so `retrying(3) { ... }` reads like a
// language construct. Returning a function builds behaviour from data, as
// multiplierOf(3) or andThen do below.

fun <T> retrying(attempts: Int, onFailure: (Int, Throwable) -> Unit = { _, _ -> }, block: (attempt: Int) -> T): T {
    require(attempts > 0)
    var last: Throwable? = null
    for (attempt in 1..attempts) {
        try {
            return block(attempt)
        } catch (e: Exception) {
            last = e
            onFailure(attempt, e)
        }
    }
    throw IllegalStateException("gave up after $attempts attempts", last)
}

fun multiplierOf(factor: Int): (Int) -> Int = { it * factor }

infix fun <A, B, C> ((A) -> B).andThen(next: (B) -> C): (A) -> C = { next(this(it)) }

fun <T> List<T>.keepIf(predicate: (T) -> Boolean): List<T> {
    val result = ArrayList<T>()
    for (item in this) if (predicate(item)) result += item
    return result
}

fun main() {
    val failures = mutableListOf<String>()
    val value = retrying(3, onFailure = { n, e -> failures += "attempt $n: ${e.message}" }) { attempt ->
        if (attempt < 3) error("timeout") else "connected on attempt $attempt"
    }
    println(value)
    println(failures)

    val triple = multiplierOf(3)
    val tripleThenDescribe = triple andThen { "result=$it" }
    println(tripleThenDescribe(14))
    println((1..10).toList().keepIf { it % 3 == 0 })
}
