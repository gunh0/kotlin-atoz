package atoz.secure

/*
Authorization as a capability, with context parameters (stable since Kotlin
2.4.0). A privileged function declares `context(admin: VerifiedAdmin)`, so it
cannot even be *called* unless a VerifiedAdmin is in scope, and the only way to
get one is the check in `VerifiedAdmin.authorize` (the constructor is private).
A forgotten `if (user.isAdmin)` becomes a compile error instead of a
vulnerability, and the proof travels implicitly through helper calls instead
of being threaded through every parameter list.
In Java you'd pass the token explicitly or rely on a thread-local "current
user"; on the JVM a context parameter *is* an ordinary leading parameter, so
Java callers must pass one too, and still can't create it.
*/

class VerifiedAdmin private constructor(val userId: String) {
    override fun toString() = "VerifiedAdmin($userId)"

    companion object {
        fun authorize(userId: String, rolesByUser: Map<String, Set<String>>): VerifiedAdmin? =
            if ("admin" in rolesByUser[userId].orEmpty()) VerifiedAdmin(userId) else null
    }
}

class AccountDirectory(users: Collection<String>) {
    private val active = users.toMutableSet()
    private val audit = mutableListOf<String>()

    val activeUsers: Set<String> get() = active.toSet()
    val auditLog: List<String> get() = audit.toList()

    context(admin: VerifiedAdmin)
    fun disable(userId: String) {
        require(userId != admin.userId) { "admins cannot disable themselves" }
        if (active.remove(userId)) audit += auditEntryFor("disabled $userId")
    }

    context(admin: VerifiedAdmin)
    fun disableAll(userIds: List<String>) = userIds.forEach { disable(it) } // the context flows on implicitly
}

context(admin: VerifiedAdmin)
fun auditEntryFor(action: String): String = "${admin.userId}: $action"

// The one place that turns a request into a capability.
fun <T> asAdmin(userId: String, rolesByUser: Map<String, Set<String>>, block: context(VerifiedAdmin) () -> T): T? {
    val admin = VerifiedAdmin.authorize(userId, rolesByUser) ?: return null
    return with(admin) { block() }
}

fun main() {
    val roles = mapOf("root" to setOf("admin"), "eve" to setOf("user"))
    val directory = AccountDirectory(listOf("root", "alice", "bob", "eve"))
    // directory.disable("bob")  // does not compile: no VerifiedAdmin in context
    println("eve: ${asAdmin("eve", roles) { directory.disable("bob") } ?: "denied"}")
    asAdmin("root", roles) { directory.disableAll(listOf("alice", "bob")) }
    println("active: ${directory.activeUsers}, audit: ${directory.auditLog}")
}
