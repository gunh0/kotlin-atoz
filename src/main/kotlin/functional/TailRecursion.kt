package atoz.functional

// The JVM does not eliminate tail calls, so deep recursion ends in a
// StackOverflowError, in Java and in plain Kotlin alike. Marking a function
// `tailrec` makes the compiler rewrite a self-call in tail position (the very
// last thing the function does) into a loop: recursive style, constant stack.
//
// The compiler checks the claim: if the recursive call is not in tail position
// (e.g. `n + sum(n - 1)`, where the addition happens after the call) it warns
// and leaves the recursion as is. The usual fix is an accumulator parameter.

fun naiveSum(n: Long): Long = if (n == 0L) 0 else n + naiveSum(n - 1)

tailrec fun tailSum(n: Long, acc: Long = 0): Long = if (n == 0L) acc else tailSum(n - 1, acc + n)

tailrec fun gcd(a: Long, b: Long): Long = if (b == 0L) a else gcd(b, a % b)

tailrec fun <T> lastMatching(items: List<T>, index: Int = items.lastIndex, predicate: (T) -> Boolean): T? = when {
    index < 0 -> null
    predicate(items[index]) -> items[index]
    else -> lastMatching(items, index - 1, predicate)
}

fun main() {
    println("tailSum(1_000_000) = ${tailSum(1_000_000)}")
    println("naiveSum(1_000_000) -> ${runCatching { naiveSum(1_000_000) }.exceptionOrNull()?.javaClass?.simpleName}")
    println("gcd(1071, 462) = ${gcd(1071, 462)}")
    println("lastMatching even: ${lastMatching(listOf(1, 2, 3, 4, 5)) { it % 2 == 0 }}")
}
