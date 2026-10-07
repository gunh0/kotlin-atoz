package atoz.modern

/*
`when` compiled with invokedynamic (stable and on by default in 2.4.20, for
JVM target 21+): a `when` that tests types (`is A ->`, `is B ->`) now
compiles to the same bytecode as a Java 21 pattern `switch`, a single
`invokedynamic` call to java.lang.runtime.SwitchBootstraps.typeSwitch that
returns the index of the first matching branch. Up to 2.4.0 it was a chain
of `instanceof` checks, tried one after another.
Nothing changes in the language: branches still match top to bottom (a
subtype listed after its supertype is never reached), `else` and `null`
behave as before. The JVM can optimize the dispatch, and the class file
looks like javac's. Checked here by reading the compiled class's bytes.
*/

sealed interface Credential {
    data class Password(val hash: String) : Credential
    data class ApiKey(val prefix: String) : Credential
    data class Passkey(val credentialId: String) : Credential
}

open class AuthFailure(message: String) : Exception(message)
class LockedOut(message: String) : AuthFailure(message)

object CredentialRouting {
    fun kind(credential: Credential): String = when (credential) {
        is Credential.Password -> "password"
        is Credential.ApiKey -> "api key ${credential.prefix}"
        is Credential.Passkey -> "passkey"
    }

    fun failureCategory(error: Throwable?): String = when (error) {
        null -> "none"
        is AuthFailure -> "auth" // listed first: LockedOut matches here, as with an instanceof chain
        is LockedOut -> "locked"
        is IllegalArgumentException -> "bad request"
        else -> "internal"
    }
}

// Does a compiled class refer to the bootstrap method javac uses for pattern switches?
// (Looked up in another class, so these string literals don't match themselves.)
fun usesTypeSwitch(type: Class<*>): Boolean {
    val bytes = type.getResourceAsStream(type.name.substringAfterLast('.') + ".class")!!.use { it.readBytes() }
    val text = String(bytes, Charsets.ISO_8859_1)
    return "java/lang/runtime/SwitchBootstraps" in text && "typeSwitch" in text
}

fun main() {
    listOf(Credential.Password("x"), Credential.ApiKey("ak_live"), Credential.Passkey("pk1")).forEach { println(CredentialRouting.kind(it)) }
    listOf(null, LockedOut("5 failures"), IllegalArgumentException(), IllegalStateException()).forEach { println(CredentialRouting.failureCategory(it)) }
    println("compiled with SwitchBootstraps.typeSwitch: ${usesTypeSwitch(CredentialRouting::class.java)}")
}
