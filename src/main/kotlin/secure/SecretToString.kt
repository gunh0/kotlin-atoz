package atoz.secure

/*
Data classes generate `toString()` from every constructor property, so a
password or token in one ends up in logs, exception messages and debugger
output the first time someone writes `log.info("login $request")`.
Java records have the same problem. Two fixes:
- Override `toString()` in the data class (easy to forget when a field is added).
- Better: wrap the secret in a value class whose `toString()` is redacted.
  Every class that holds it is then safe by default, and the wrapper costs
  nothing at runtime (`@JvmInline` compiles to the underlying String).
`componentN()` and `copy()` still expose the value on purpose; redaction only
protects against accidental formatting. For passwords that must be wiped
from memory, use CharArray and clear it; a String can't be erased.
*/

data class LeakyCredentials(val user: String, val password: String)

data class RedactedCredentials(val user: String, val password: String) {
    override fun toString() = "RedactedCredentials(user=$user, password=****)"
}

@JvmInline
value class Secret(private val value: String) {
    fun reveal(): String = value

    override fun toString() = "Secret(****)"
}

data class ApiClientConfig(val endpoint: String, val apiKey: Secret)

fun main() {
    println(LeakyCredentials("alice", "hunter2"))
    println(RedactedCredentials("alice", "hunter2"))
    val config = ApiClientConfig("https://api.example.com", Secret("sk_live_abc123"))
    println("config: $config")
    println("explicit use: ${config.apiKey.reveal().take(3)}...")
}
