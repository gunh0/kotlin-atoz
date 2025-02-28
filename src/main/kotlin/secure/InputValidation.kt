package atoz.secure

/*
Null safety removes NPEs inside Kotlin code, but not at the boundary: values
from Java APIs (platform types), reflection, deserializers and environment
variables can still be null, and nothing checks *ranges* or *formats* for you.
Validate once, where data enters, and let the types carry the guarantee:
- `requireNotNull(x) { }` / `require(cond) { }` reject bad arguments with
  IllegalArgumentException (Java: Objects.requireNonNull + manual checks).
- `check(cond) { }` / `checkNotNull` guard the object's own state and throw
  IllegalStateException, so callers can tell "you passed garbage" apart from
  "this object is in the wrong state".
The message lambdas are only evaluated on failure. Never echo raw secrets in them.
*/

data class ListenAddress(val host: String, val port: Int)

private val hostPattern = Regex("[A-Za-z0-9.-]{1,253}")

fun parseListenAddress(raw: String?): ListenAddress {
    val value = requireNotNull(raw) { "listen address is not configured" }
    val host = value.substringBeforeLast(":", missingDelimiterValue = "")
    val port = value.substringAfterLast(":").toIntOrNull()
    require(host.matches(hostPattern)) { "invalid host in listen address" }
    require(port != null && port in 1..65535) { "port must be 1..65535" }
    return ListenAddress(host, port) // smart cast: port is Int here
}

// The environment is a typical boundary: System.getenv returns a platform type.
fun listenAddressFromEnv(env: (String) -> String? = System::getenv): ListenAddress =
    parseListenAddress(env("ATOZ_LISTEN"))

class Account(private var balance: Long) {
    var closed = false
        private set

    fun withdraw(amount: Long): Long {
        require(amount > 0) { "amount must be positive, was $amount" }
        check(!closed) { "account is closed" }
        check(amount <= balance) { "insufficient funds" }
        balance -= amount
        return balance
    }

    fun close() {
        closed = true
    }
}

fun main() {
    println(parseListenAddress("api.internal:8443"))
    for (bad in listOf(null, "api.internal", "api internal:80", "api:99999")) {
        println("$bad -> ${runCatching { parseListenAddress(bad) }.exceptionOrNull()}")
    }
    val account = Account(100)
    println(account.withdraw(30))
    println(runCatching { account.withdraw(-5) }.exceptionOrNull())
    account.close()
    println(runCatching { account.withdraw(5) }.exceptionOrNull())
}
