package atoz.functional

// An `inline` function is copied into each call site together with the lambdas
// passed to it, so no Function object is allocated and no virtual call is made.
// It also allows a *non-local return*: a bare `return` inside the lambda leaves
// the enclosing function, which is why `return` works inside forEach.
//
// `noinline` keeps one lambda parameter as a real object (needed to store it
// or pass it on). `crossinline` keeps the lambda inlined but forbids non-local
// returns, required when the lambda is called from another execution context,
// such as inside an object or a Runnable created by the inline function.
// Inline only small functions that take lambdas; inlining big bodies bloats code.

inline fun <T> timed(log: MutableList<String>, label: String, block: () -> T): T {
    log += "start $label"
    try {
        return block()
    } finally {
        log += "end $label"
    }
}

fun firstNegative(numbers: List<Int>): Int? {
    numbers.forEach { if (it < 0) return it } // non-local: returns from firstNegative
    return null
}

fun firstNegativeIndex(numbers: List<Int>, log: MutableList<String>): Int {
    numbers.forEachIndexed { index, n ->
        timed(log, "check $index") {
            if (n < 0) return index // still leaves firstNegativeIndex, finally blocks run
        }
    }
    return -1
}

inline fun registerHandlers(handlers: MutableList<() -> Unit>, now: () -> Unit, noinline later: () -> Unit) {
    now()
    handlers += later // only possible because `later` is noinline
}

inline fun asTask(crossinline action: () -> Unit): Runnable = Runnable { action() }

fun main() {
    println("firstNegative = ${firstNegative(listOf(3, 1, -4, -1))}")
    val log = mutableListOf<String>()
    println("firstNegativeIndex = ${firstNegativeIndex(listOf(5, -2, 7), log)}, log = $log")

    val handlers = mutableListOf<() -> Unit>()
    registerHandlers(handlers, { println("runs now") }, { println("runs later") })
    handlers.forEach { it() }

    var ran = false
    val task = asTask { ran = true }
    task.run()
    println("task ran: $ran")
}
