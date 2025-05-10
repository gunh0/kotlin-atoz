package atoz.secure

/*
`val` and `List` make a reference read-only, not the object behind it.
A data class that stores the caller's MutableList can change after it has been
validated: the caller keeps a reference and mutates it later (a time-of-check
to time-of-use bug, the same trap as storing a Java array or ArrayList).
- Copy on the way in (`toList()`, `toSet()`) and validate the copy.
- Copy on the way out if you hold anything mutable.
- Arrays are always mutable, and data classes compare them by identity, so
  prefer immutable wrappers, or copy and override equals yourself.
A private constructor plus a factory makes the copy impossible to skip.
*/

class Grant private constructor(val user: String, val roles: Set<String>) {
    companion object {
        private val allowedRoles = setOf("reader", "writer", "admin")

        fun of(user: String, roles: Collection<String>): Grant {
            val snapshot = roles.toSet() // copy first, then validate what we keep
            require(snapshot.all { it in allowedRoles }) { "unknown role in $snapshot" }
            require("admin" !in snapshot || user.endsWith("@ops")) { "admin is reserved for ops accounts" }
            return Grant(user, snapshot)
        }
    }

    fun can(role: String) = role in roles
}

// The naive version: validated once, then trusts a list the caller still owns.
class NaiveGrant(val user: String, val roles: List<String>) {
    init {
        require("admin" !in roles || user.endsWith("@ops")) { "admin is reserved for ops accounts" }
    }

    fun can(role: String) = role in roles
}

data class KeyMaterial(val bytes: ByteArray) // equals/hashCode use array identity: a warning sign

fun main() {
    val requested = mutableListOf("reader")
    val naive = NaiveGrant("mallory@dev", requested)
    val safe = Grant.of("mallory@dev", requested)
    requested += "admin" // after validation
    println("naive grant can admin: ${naive.can("admin")}")
    println("safe grant can admin: ${safe.can("admin")}")
    println("equal keys? ${KeyMaterial(byteArrayOf(1, 2)) == KeyMaterial(byteArrayOf(1, 2))}")
}
