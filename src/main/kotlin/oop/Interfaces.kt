package atoz.oop

// Interfaces can declare abstract members, provide default implementations
// (like Java 8 default methods) and declare properties. An interface holds no
// state, so a property is either abstract or computed by a getter.
//
// When a class inherits two defaults with the same signature, the compiler
// insists on an explicit override, which can call each parent with
// `super<Name>.member()`. `override` is a required keyword, not an optional
// annotation as @Override is in Java.

interface Named {
    val name: String
    val displayName: String get() = name.replaceFirstChar { it.uppercase() }
}

interface Auditable {
    fun describe(): String = "audited"
}

interface Expirable {
    val ttlSeconds: Long
    fun describe(): String = "expires in ${ttlSeconds}s"
    fun isExpired(ageSeconds: Long): Boolean = ageSeconds >= ttlSeconds
}

class CacheEntry(override val name: String, override val ttlSeconds: Long) : Named, Auditable, Expirable {
    override fun describe(): String = "$displayName: ${super<Auditable>.describe()}, ${super<Expirable>.describe()}"
}

class Permanent(override val name: String) : Named, Expirable {
    override val ttlSeconds: Long = Long.MAX_VALUE
    override fun isExpired(ageSeconds: Long): Boolean = false
}

fun main() {
    val entry = CacheEntry("token", 60)
    println(entry.describe())
    println("expired after 90s: ${entry.isExpired(90)}")
    val permanent = Permanent("config")
    println("${permanent.displayName}: ${permanent.describe()}, expired: ${permanent.isExpired(Long.MAX_VALUE)}")
}
