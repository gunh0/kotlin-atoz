package atoz.functional

// Currying turns a function of several arguments into a chain of one-argument
// functions: (A, B) -> C becomes (A) -> (B) -> C. Partial application fixes
// some arguments now and leaves the rest for later. Kotlin has no built-in
// syntax for either, but function types and extension functions make both a
// few lines, and a capturing lambda is often all you need.
//
// It shines for configuration: fix the logger's level, the request's base URL
// or the hash function's key once, and pass the resulting smaller function to
// code that should not know about those details.

fun <A, B, C> ((A, B) -> C).curried(): (A) -> (B) -> C = { a -> { b -> this(a, b) } }

fun <A, B, C> ((A, B) -> C).partial(first: A): (B) -> C = { b -> this(first, b) }

fun <A, B, C> ((A) -> (B) -> C).uncurried(): (A, B) -> C = { a, b -> this(a)(b) }

fun formatLog(level: String, message: String): String = "[$level] $message"

fun buildUrl(base: String, path: String): String = base.trimEnd('/') + "/" + path.trimStart('/')

val logAt: (String) -> (String) -> String = ::formatLog.curried()
val warn: (String) -> String = logAt("WARN")
val apiUrl: (String) -> String = ::buildUrl.partial("https://api.example.com/v1/")

fun main() {
    println(warn("disk almost full"))
    println(logAt("ERROR")("connection refused"))
    println(apiUrl("/users/42"))
    val plain: (String, String) -> String = logAt.uncurried()
    println(plain("INFO", "back to two arguments"))
    println(listOf("a", "b").map(logAt("DEBUG")))
}
