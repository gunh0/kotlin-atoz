package atoz.functional

/*
Combinators: small functions that build bigger values of the same type. Here
an access policy is assembled from tiny rules with `and`, `or` and `!`,
instead of one growing if/else block that nobody dares to touch.
`fun interface Rule<in T>` rather than `typealias Rule<T> = (T) -> Boolean`:
- it is a named type of its own, so the `and`/`or` extensions don't show up
  on every Boolean-returning lambda in the code base;
- a lambda still converts to it (`Rule { it.hour in 9..17 }`), and Java code
  can implement it like java.util.function.Predicate;
- `in T` (contravariance) lets a `Rule<Any?>` serve as a `Rule<AccessRequest>`.
The same idea as Java's Predicate.and/or/negate. Empty combinations deny:
`anyOf()` of nothing is false, and `allOf()` of nothing refuses to build.
*/

fun interface Rule<in T> {
    fun allows(subject: T): Boolean
}

infix fun <T> Rule<T>.and(other: Rule<T>): Rule<T> = Rule { allows(it) && other.allows(it) }

infix fun <T> Rule<T>.or(other: Rule<T>): Rule<T> = Rule { allows(it) || other.allows(it) }

operator fun <T> Rule<T>.not(): Rule<T> = Rule { !allows(it) }

// An empty allOf would allow everything; refuse to build one by accident.
fun <T> allOf(vararg rules: Rule<T>): Rule<T> {
    require(rules.isNotEmpty()) { "allOf() needs at least one rule" }
    return rules.reduce { acc, rule -> acc and rule }
}

fun <T> anyOf(vararg rules: Rule<T>): Rule<T> = Rule { subject -> rules.any { it.allows(subject) } }

data class AccessRequest(val user: String, val roles: Set<String>, val ip: String, val hour: Int)

object AccessRules {
    val denyAll = Rule<Any?> { false }
    val isAdmin = Rule<AccessRequest> { "admin" in it.roles }
    val isAuditor = Rule<AccessRequest> { "auditor" in it.roles }
    val fromOfficeNetwork = Rule<AccessRequest> { it.ip.startsWith("10.20.") }
    val duringOfficeHours = Rule<AccessRequest> { it.hour in 9..17 }
    val isServiceAccount = Rule<AccessRequest> { it.user.startsWith("svc-") }

    val readAuditLog: Rule<AccessRequest> =
        (isAdmin or isAuditor) and (fromOfficeNetwork or duringOfficeHours) and !isServiceAccount
}

fun main() {
    val requests = listOf(
        AccessRequest("alice", setOf("admin"), "10.20.1.5", hour = 23),
        AccessRequest("bob", setOf("auditor"), "203.0.113.9", hour = 11),
        AccessRequest("bob", setOf("auditor"), "203.0.113.9", hour = 3),
        AccessRequest("svc-backup", setOf("admin"), "10.20.0.2", hour = 2),
    )
    for (r in requests) println("${r.user}@${r.ip} ${r.hour}h -> ${AccessRules.readAuditLog.allows(r)}")
    val lockdown: Rule<AccessRequest> = AccessRules.denyAll // Rule<Any?> used as Rule<AccessRequest>
    println("lockdown allows alice: ${lockdown.allows(requests[0])}")
    println("anyOf() with no rules: ${anyOf<AccessRequest>().allows(requests[0])}")
}
