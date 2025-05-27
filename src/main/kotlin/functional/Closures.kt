package atoz.functional

// A lambda closes over the variables in scope where it is created. Unlike
// Java, where captured locals must be effectively final, Kotlin lambdas can
// read *and modify* captured `var`s (the compiler moves them into a shared
// box, Ref.IntRef). The lambda sees the variable, not a copy of its value.
//
// Each call of a function that returns a lambda creates new captured state, so
// two counters don't interfere. A `for` loop declares a fresh `val` per
// iteration, so lambdas created in a loop capture distinct values; the
// JavaScript `var` pitfall doesn't exist.

fun counter(start: Int = 0): () -> Int {
    var count = start
    return { ++count }
}

fun sumWithForEach(numbers: List<Int>): Int {
    var total = 0
    numbers.forEach { total += it } // modifying a captured local, illegal in Java
    return total
}

fun captureLatest(): Pair<String, String> {
    var mode = "dev"
    val describe = { "mode=$mode" }
    val before = describe()
    mode = "prod"
    return before to describe() // the lambda sees the current value
}

fun lambdasFromLoop(): List<() -> Int> {
    val result = mutableListOf<() -> Int>()
    for (i in 1..3) result += { i * 10 }
    return result
}

fun rateLimiter(maxCalls: Int): (String) -> Boolean {
    val seen = mutableMapOf<String, Int>()
    return { client ->
        val calls = (seen[client] ?: 0) + 1
        seen[client] = calls
        calls <= maxCalls
    }
}

fun main() {
    val a = counter()
    val b = counter(100)
    println("a: ${a()}, ${a()}, ${a()}  b: ${b()}")
    println("sum = ${sumWithForEach(listOf(1, 2, 3, 4))}")
    println("capture: ${captureLatest()}")
    println("from loop: ${lambdasFromLoop().map { it() }}")
    val allow = rateLimiter(2)
    println("client-a: ${List(3) { allow("client-a") }}, client-b: ${allow("client-b")}")
}
