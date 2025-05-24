package atoz.basics

// Functions are declared with `fun`, may live at top level and may be a single
// expression (`= ...`, return type inferred). Default arguments replace most of
// Java's telescoping overloads, and named arguments make call sites readable
// and let you skip defaults in the middle: connectionUrl("db", tls = false).
//
// `vararg` collects arguments into an array; the spread operator `*` passes an
// existing array as varargs and can be mixed with plain values. A function can
// declare local functions that see the enclosing parameters.

fun connectionUrl(host: String, port: Int = 5432, tls: Boolean = true, database: String = host): String {
    val scheme = if (tls) "postgresql+tls" else "postgresql"
    return "$scheme://$host:$port/$database"
}

fun square(x: Int) = x * x

fun sumAll(vararg numbers: Int): Int = numbers.sum()

fun joinPath(separator: String = "/", vararg parts: String): String = parts.joinToString(separator)

fun validPorts(candidates: List<Int>): List<Int> {
    fun inRange(port: Int) = port in 1..65535
    fun notPrivileged(port: Int) = port >= 1024
    return candidates.filter { inRange(it) && notPrivileged(it) }
}

fun main() {
    println(connectionUrl("orders"))
    println(connectionUrl("orders", tls = false))
    println(connectionUrl(host = "replica", database = "orders", port = 6432))
    println("square(12) = ${square(12)}")
    val extra = intArrayOf(4, 5)
    println("sumAll(1, 2, 3, *extra) = ${sumAll(1, 2, 3, *extra)}")
    println(joinPath(parts = arrayOf("usr", "local", "bin")))
    println(validPorts(listOf(22, 8080, 70000, 443, 5432)))
}
