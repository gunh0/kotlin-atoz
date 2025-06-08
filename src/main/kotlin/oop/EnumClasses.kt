package atoz.oop

// Enum classes work like Java's: a fixed set of instances that can have
// constructor properties, methods and per-constant overrides of abstract
// members. Constants compare by declaration order (compareTo uses ordinal).
//
// `entries` (Kotlin 1.9) is an immutable, pre-allocated list of the constants;
// prefer it over values(), which allocates a new array on every call.
// valueOf() throws IllegalArgumentException for unknown names, so input from
// outside is better looked up with entries.find { ... }, which yields null.
// A `when` over an enum is exhaustive without `else`.

enum class Severity(val weight: Int) {
    LOW(1),
    MEDIUM(4),
    HIGH(7),
    CRITICAL(10);

    fun atLeast(other: Severity): Boolean = this >= other

    companion object {
        fun parse(raw: String): Severity? = entries.find { it.name.equals(raw.trim(), ignoreCase = true) }
        fun fromScore(score: Double): Severity = entries.last { score >= it.weight || it == LOW }
    }
}

enum class Protocol {
    HTTP {
        override fun defaultPort() = 80
    },
    HTTPS {
        override fun defaultPort() = 443
    },
    SSH {
        override fun defaultPort() = 22
    };

    abstract fun defaultPort(): Int
}

fun isEncrypted(protocol: Protocol): Boolean = when (protocol) {
    Protocol.HTTP -> false
    Protocol.HTTPS, Protocol.SSH -> true
}

fun main() {
    println("entries: ${Severity.entries}")
    println("HIGH.atLeast(MEDIUM) = ${Severity.HIGH.atLeast(Severity.MEDIUM)}, ordinal = ${Severity.HIGH.ordinal}")
    println("parse(\" critical \") = ${Severity.parse(" critical ")}, parse(\"urgent\") = ${Severity.parse("urgent")}")
    println("valueOf(\"urgent\") -> ${runCatching { Severity.valueOf("urgent") }.exceptionOrNull()}")
    println("CVSS 7.5 -> ${Severity.fromScore(7.5)}, 0.3 -> ${Severity.fromScore(0.3)}")
    Protocol.entries.forEach { println("$it: port ${it.defaultPort()}, encrypted ${isEncrypted(it)}") }
}
