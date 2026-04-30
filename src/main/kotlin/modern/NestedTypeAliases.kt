package atoz.modern

/*
Nested type aliases (stable in 2.3): a `typealias` can now be declared inside
a class, object or interface, not only at the top level of a file. Before,
every alias landed in the package namespace, so two classes that each wanted a
short `Handler` alias had to invent `HttpHandler`, `EventHandler`, ...
A nested alias is referenced like a nested class (`AccessPolicy.Rule`) and
can be `private` to keep a helper type out of the public API.
An alias is still only a second name: `(String, String) -> Boolean` and
`AccessPolicy.Rule` are the same type and freely interchangeable (unlike a
value class). A nested alias can't use the outer class's type parameters.
Java has no type aliases at all; the JVM signature shows the expanded type.
*/

class AccessPolicy(private val rules: RuleTable) {
    typealias Rule = (user: String, action: String) -> Boolean
    typealias RuleTable = Map<String, Rule>

    private typealias Decision = Pair<String, Boolean>

    private fun decide(user: String, action: String): Decision {
        val rule = rules[action] ?: return "no rule for $action" to false // deny by default
        return "rule for $action" to rule(user, action)
    }

    fun isAllowed(user: String, action: String): Boolean = decide(user, action).second

    fun explain(user: String, action: String): String = decide(user, action).let { (why, ok) -> "$why: ${if (ok) "allow" else "deny"}" }
}

// Another class with its own `Rule`: no clash, each alias lives in its class.
object PasswordPolicy {
    typealias Rule = (password: String) -> Boolean

    val rules: List<Rule> = listOf({ it.length >= 12 }, { p -> p.any(Char::isDigit) })

    fun accepts(password: String): Boolean = rules.all { it(password) }
}

fun main() {
    val adminsOnly: AccessPolicy.Rule = { user, _ -> user == "root" }
    val policy = AccessPolicy(mapOf("delete" to adminsOnly, "read" to { _, _ -> true }))
    println(policy.explain("alice", "read"))
    println(policy.explain("alice", "delete"))
    println(policy.explain("alice", "export"))
    println("password ok: ${PasswordPolicy.accepts("correct horse 42")}")
}
