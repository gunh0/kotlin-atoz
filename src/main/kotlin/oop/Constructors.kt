package atoz.oop

// The primary constructor is part of the class header. Marking a parameter
// `val`/`var` declares and assigns a property in one go, replacing Java's field
// + constructor + getter boilerplate; a parameter without val/var is only
// visible to initializers. Default values (which may refer to earlier
// parameters) replace telescoping constructors, and there is no `new`.
//
// Secondary constructors (`constructor(...)`) must delegate to the primary one
// with `: this(...)`. A class without a primary constructor can declare only
// secondary ones, Java style, though that is rarely needed.

class Endpoint(val host: String, val port: Int = 443, val secure: Boolean = port == 443) {
    constructor(hostAndPort: Pair<String, Int>) : this(hostAndPort.first, hostAndPort.second)

    fun url(): String = (if (secure) "https" else "http") + "://$host:$port"
}

class Greeter(name: String) {
    private val message = "Hello, ${name.trim()}" // `name` is not a property
    fun greet(): String = message
}

class LegacyCounter {
    val start: Int

    constructor() {
        start = 0
    }

    constructor(start: Int) {
        this.start = start
    }
}

fun main() {
    println(Endpoint("example.com").url())
    println(Endpoint("localhost", 8080).url())
    println(Endpoint("internal", 8443, secure = true).url())
    println(Endpoint("pair.example" to 80).url())
    println(Greeter("  Kotlin ").greet())
    println("LegacyCounter().start = ${LegacyCounter().start}, LegacyCounter(5).start = ${LegacyCounter(5).start}")
}
