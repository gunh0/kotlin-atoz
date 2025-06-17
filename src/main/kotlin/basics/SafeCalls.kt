package atoz.basics

// Safe calls chain: `a?.b?.c` stops at the first null and the whole chain is
// null, replacing Java's ladder of `if (a != null && a.getB() != null ...)`.
//
// `?.let { ... }` runs a block only for a non-null value and passes it as `it`
// (already smart-cast to the non-null type); combined with `?:` it gives an
// "if present, else" in one expression. takeIf/takeUnless turn a value into
// null when a predicate fails, which lets validation join the same chain.
// mapNotNull applies a nullable transformation and drops the nulls.

class ProfileAddress(val city: String?, val zip: String?)
class Profile(val name: String, val address: ProfileAddress?)

fun cityLabel(profile: Profile?): String =
    profile?.address?.city?.uppercase() ?: "UNKNOWN"

fun greetingFor(profile: Profile?): String =
    profile?.name?.let { "Welcome back, $it" } ?: "Welcome, guest"

fun validZip(profile: Profile?): String? =
    profile?.address?.zip?.trim()?.takeIf { it.length == 5 && it.all(Char::isDigit) }

fun parsePorts(raw: List<String>): List<Int> =
    raw.mapNotNull { it.toIntOrNull()?.takeIf { port -> port in 1..65535 } }

fun main() {
    val full = Profile("ian", ProfileAddress("seoul", " 04524 "))
    val noAddress = Profile("guest", null)
    println("${cityLabel(full)} / ${cityLabel(noAddress)} / ${cityLabel(null)}")
    println("${greetingFor(full)} / ${greetingFor(null)}")
    println("zip: ${validZip(full)}, zip without address: ${validZip(noAddress)}")
    println(parsePorts(listOf("80", "http", "443", "70000")))
}
