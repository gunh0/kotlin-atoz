package atoz.secure

/*
Parse untrusted input strictly and into a sealed result, not leniently into
a half-filled object:
- Cap the size before looking at the content.
- Reject unknown fields (mass assignment: "role=admin" sneaking into a form)
  and duplicate ones (parameter pollution: one component reads the first
  value, another the last).
- Map enum input through an explicit allowlist. `Enum.valueOf` accepts every
  constant, including ones users must never pick, and its exception echoes
  the input; case-insensitive variants of it are even more permissive.
- Check numbers with an exact ASCII-digit rule: `toIntOrNull` accepts a
  leading '+' and, on the JVM, any Unicode digit (Character.digit), e.g. "٣".
Rejection reasons are fixed strings, never the raw input.
*/

enum class InviteRole { VIEWER, EDITOR, ADMIN }

data class Invite(val email: String, val role: InviteRole, val days: Int)

sealed interface InviteParse {
    data class Accepted(val invite: Invite) : InviteParse
    data class Rejected(val reason: String) : InviteParse
}

object InviteParser {
    const val MAX_INPUT = 1_024
    private val allowedFields = setOf("email", "role", "days")
    private val selfServiceRoles = mapOf("viewer" to InviteRole.VIEWER, "editor" to InviteRole.EDITOR)
    private val emailPattern = Regex("[A-Za-z0-9._%+-]{1,64}@[A-Za-z0-9.-]{1,253}\\.[A-Za-z]{2,24}")
    private val asciiNumber = Regex("[0-9]{1,3}")

    fun parse(raw: String): InviteParse {
        if (raw.length > MAX_INPUT) return InviteParse.Rejected("input too long")
        val fields = mutableMapOf<String, String>()
        for (line in raw.lineSequence().filter { it.isNotBlank() }) {
            val key = line.substringBefore('=', missingDelimiterValue = "")
            if (key !in allowedFields) return InviteParse.Rejected("unknown field")
            if (fields.put(key, line.substringAfter('=')) != null) return InviteParse.Rejected("duplicate field")
        }
        val email = fields["email"]?.takeIf { it.matches(emailPattern) } ?: return InviteParse.Rejected("invalid email")
        val role = selfServiceRoles[fields["role"]] ?: return InviteParse.Rejected("role not allowed")
        val days = fields["days"]?.takeIf { it.matches(asciiNumber) }?.toInt()?.takeIf { it in 1..30 }
            ?: return InviteParse.Rejected("days must be 1..30")
        return InviteParse.Accepted(Invite(email, role, days))
    }
}

// The lenient version this replaces.
fun lenientRole(input: String): InviteRole = InviteRole.valueOf(input.trim().uppercase())

fun main() {
    val inputs = listOf(
        "email=ana@example.com\nrole=editor\ndays=7",
        "email=ana@example.com\nrole=admin\ndays=7",
        "email=ana@example.com\nrole=viewer\ndays=7\nisAdmin=true",
        "email=ana@example.com\nrole=viewer\nrole=editor\ndays=7",
        "email=ana@example.com\nrole=viewer\ndays=٣",
    )
    for (input in inputs) println("${input.replace('\n', '&')} -> ${InviteParser.parse(input)}")
    println("lenient valueOf grants: ${lenientRole(" admin ")}")
}
