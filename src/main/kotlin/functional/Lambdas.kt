package atoz.functional

// Functions are values. A function type is written `(Int, Int) -> Int`; a
// lambda is `{ a, b -> a + b }` and its last expression is its result (no
// `return`). A single parameter can stay implicit as `it`, and `_` names a
// parameter you ignore. Function types can be nullable, `((String) -> Unit)?`,
// and called with `?.invoke(...)`.
//
// Compared to Java there are no functional interfaces to pick (Function,
// BiFunction, Supplier, ...): one notation covers every arity. An anonymous
// function, `fun(x: Int): Int { ... }`, is the alternative when you want an
// explicit return type or a `return` that only leaves the function itself.

val add: (Int, Int) -> Int = { a, b -> a + b }
val square = { x: Int -> x * x }
val isBlankLine: (String) -> Boolean = { it.isBlank() }
val firstOfPair: (Pair<String, Int>, Int) -> String = { pair, _ -> pair.first }

val clampToByte = fun(x: Int): Int {
    if (x < 0) return 0
    if (x > 255) return 255
    return x
}

fun notifyIfSet(listener: ((String) -> Unit)?, message: String): Boolean {
    listener?.invoke(message)
    return listener != null
}

val classify: (Int) -> String = { n ->
    val sign = if (n < 0) "negative" else "non-negative"
    val parity = if (n % 2 == 0) "even" else "odd"
    "$sign $parity" // the last expression is the result
}

fun main() {
    println("add(2, 3) = ${add(2, 3)}, square(7) = ${square(7)}")
    println("blank lines: ${listOf("a", "  ", "", "b").count(isBlankLine)}")
    println("clampToByte: ${listOf(-5, 100, 300).map(clampToByte)}")
    println("classify(-3) = ${classify(-3)}")
    val received = mutableListOf<String>()
    notifyIfSet({ received += it }, "deployed")
    notifyIfSet(null, "ignored")
    println("received: $received")
}
