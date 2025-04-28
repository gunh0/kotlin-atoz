package atoz.oop

// Four modifiers, public by default (Java defaults to package-private):
// - private: inside the class, or inside the file for top-level declarations
// - protected: the class and its subclasses only; unlike Java, NOT the package
// - internal: anywhere in the same module (Gradle source set / compilation)
// - public: everywhere
// Kotlin has no package-private at all; `internal` is the closest tool.
// A private constructor plus a companion factory is the usual way to make
// sure every instance has passed validation.

open class AuditedStore {
    private val items = mutableMapOf<String, String>()
    protected val auditLog = mutableListOf<String>()

    fun put(key: String, value: String) {
        items[key] = value
        auditLog += "put $key=$value"
    }

    fun get(key: String): String? = items[key]

    internal fun size(): Int = items.size

    open fun auditTrail(): List<String> = auditLog.toList()
}

class RedactingStore : AuditedStore() {
    override fun auditTrail(): List<String> = auditLog.map { it.substringBefore('=') + "=***" }
}

class ApiKey private constructor(val value: String) {
    override fun toString(): String = "ApiKey(${value.take(4)}...)"

    companion object {
        private val format = Regex("[A-Za-z0-9]{32}")

        fun parse(raw: String): ApiKey? = raw.trim().takeIf(format::matches)?.let(::ApiKey)
    }
}

private fun visibilityDemoKey(): String = "a".repeat(16) + "B".repeat(16) // file-private

fun main() {
    val store = RedactingStore()
    store.put("db.password", "hunter2")
    println("get: ${store.get("db.password")}, size: ${store.size()}")
    println("audit trail: ${store.auditTrail()}")
    println("parse valid: ${ApiKey.parse(visibilityDemoKey())}, parse invalid: ${ApiKey.parse("short")}")
}
