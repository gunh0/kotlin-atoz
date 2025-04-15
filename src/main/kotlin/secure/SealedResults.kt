package atoz.secure

/*
Expected failures are not exceptional. A login with a wrong password is an
everyday outcome; modelling it as a sealed type instead of an exception
- makes every outcome visible in the signature (Java's checked exceptions
  tried this, but callers routinely wrapped or swallowed them),
- lets `when` be exhaustive: adding a new outcome breaks every caller that
  forgot to handle it, at compile time, with no `else` branch needed,
- keeps internal detail (which check failed) in the type for logging while the
  public message stays uniform, so an attacker can't enumerate accounts by
  comparing "unknown user" with "wrong password".
Keep exceptions for bugs and broken infrastructure.
*/

sealed interface LoginResult {
    data class Success(val user: String) : LoginResult
    data object UnknownUser : LoginResult
    data object WrongPassword : LoginResult
    data class Locked(val failedAttempts: Int) : LoginResult
}

class LoginService(private val passwords: Map<String, String>, private val maxAttempts: Int = 3) {
    private val failures = mutableMapOf<String, Int>()

    fun login(user: String, password: String): LoginResult {
        val attempts = failures[user] ?: 0
        if (attempts >= maxAttempts) return LoginResult.Locked(attempts)
        val expected = passwords[user] ?: return LoginResult.UnknownUser
        // Real code compares salted password hashes in constant time, never plain strings.
        if (expected != password) {
            failures[user] = attempts + 1
            return LoginResult.WrongPassword
        }
        failures.remove(user)
        return LoginResult.Success(user)
    }
}

// What the client sees: exhaustive, and identical for UnknownUser and WrongPassword.
fun publicMessage(result: LoginResult): String = when (result) {
    is LoginResult.Success -> "Welcome, ${result.user}"
    LoginResult.UnknownUser, LoginResult.WrongPassword -> "Invalid username or password"
    is LoginResult.Locked -> "Too many attempts, try again later"
}

// What the audit log sees.
fun auditEntry(user: String, result: LoginResult): String = when (result) {
    is LoginResult.Success -> "login ok user=$user"
    LoginResult.UnknownUser -> "login failed reason=unknown-user"
    LoginResult.WrongPassword -> "login failed reason=wrong-password user=$user"
    is LoginResult.Locked -> "login blocked user=$user attempts=${result.failedAttempts}"
}

fun main() {
    val service = LoginService(mapOf("alice" to "correct horse"))
    for ((user, password) in listOf("alice" to "guess", "bob" to "guess", "alice" to "correct horse")) {
        val result = service.login(user, password)
        println("${publicMessage(result)} | ${auditEntry(user, result)}")
    }
}
