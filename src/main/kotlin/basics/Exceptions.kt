package atoz.basics

// Kotlin has no checked exceptions: nothing forces a `throws` clause or a
// catch, which keeps lambdas usable but means you must read the docs.
//
// `try` is an expression, so a fallback value comes straight out of it.
// runCatching wraps a block into a Result holding a value or the error. The
// precondition helpers state intent and pick the exception type for you:
// require() -> IllegalArgumentException (bad argument), check() ->
// IllegalStateException (bad state), error() -> IllegalStateException always.
// `use` closes a Closeable like Java's try-with-resources.

fun parsePortOr(raw: String, fallback: Int): Int =
    try {
        raw.trim().toInt()
    } catch (e: NumberFormatException) {
        fallback
    }

fun parsePortOrNull(raw: String): Int? = runCatching { raw.toInt() }.getOrNull()

class Wallet(balance: Long) {
    var balance: Long = balance
        private set
    var closed = false
        private set

    fun withdraw(amount: Long) {
        require(amount > 0) { "amount must be positive, was $amount" }
        check(!closed) { "wallet is closed" }
        if (amount > balance) error("insufficient funds")
        balance -= amount
    }

    fun close() {
        closed = true
    }
}

class TrackedResource(private val log: MutableList<String>) : AutoCloseable {
    fun read(): String = "data".also { log += "read" }
    override fun close() {
        log += "closed"
    }
}

fun readAndClose(log: MutableList<String>, fail: Boolean): String =
    TrackedResource(log).use {
        val data = it.read()
        if (fail) throw IllegalStateException("boom")
        data
    }

fun main() {
    println("parsePortOr(\" 8080 \") = ${parsePortOr(" 8080 ", 80)}, parsePortOr(\"x\") = ${parsePortOr("x", 80)}")
    println("parsePortOrNull(\"x\") = ${parsePortOrNull("x")}")
    val wallet = Wallet(100)
    listOf(-5L, 30L, 500L).forEach { amount ->
        val outcome = runCatching { wallet.withdraw(amount) }
        println("withdraw($amount): ${outcome.exceptionOrNull() ?: "ok, balance ${wallet.balance}"}")
    }
    wallet.close()
    println("after close: ${runCatching { wallet.withdraw(1) }.exceptionOrNull()}")
    val log = mutableListOf<String>()
    runCatching { readAndClose(log, fail = true) }
    println("use() still closed the resource: $log")
}
