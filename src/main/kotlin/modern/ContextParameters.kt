package atoz.modern

/*
Context parameters (stable in 2.4): `context(audit: AuditTrail) fun f()`
declares a parameter that callers don't pass by hand. It is taken from the
context at the call site: a `with(trail) { }` block, the stdlib
`context(trail) { }` function, or the caller's own context parameters, so a
dependency flows through a call chain without being threaded through every
signature. Unlike an extension receiver it is named and doesn't become `this`.
They replace the experimental context receivers (`context(AuditTrail)`), which
2.3.20 removed. Explicit context arguments at the call site are still
experimental.
On the JVM a context parameter is just a leading method parameter, so Java
callers pass it explicitly. Java's closest idiom is a ThreadLocal or a
parameter added to every method.
*/

interface AuditTrail {
    fun record(entry: String)
}

class InMemoryAuditTrail : AuditTrail {
    val entries = mutableListOf<String>()

    override fun record(entry: String) {
        entries += entry
    }
}

context(audit: AuditTrail)
fun transferFunds(from: String, to: String, cents: Long): Boolean {
    if (cents <= 0) {
        audit.record("rejected transfer $from->$to")
        return false
    }
    audit.record("transfer $cents $from->$to")
    return true
}

// The caller's context parameter satisfies transferFunds' context implicitly.
context(audit: AuditTrail)
fun closeAccount(id: String, balanceCents: Long) {
    transferFunds(id, "payout", balanceCents)
    audit.record("closed $id")
}

// `_` when the body only forwards the context and never names it.
context(_: AuditTrail)
fun closeAll(balances: Map<String, Long>) = balances.forEach { (id, cents) -> closeAccount(id, cents) }

fun main() {
    val trail = InMemoryAuditTrail()
    with(trail) {
        closeAccount("acc-1", 1_500)
    }
    context(trail) {
        closeAll(mapOf("acc-2" to 0, "acc-3" to 99))
    }
    trail.entries.forEach(::println)
}
