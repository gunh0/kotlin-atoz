package atoz.secure

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/*
Store passwords as slow, salted hashes: never encrypted, never a plain SHA-256
(a GPU tries billions of those per second). PBKDF2 ships with the JDK
(javax.crypto): it repeats HMAC-SHA256 `iterations` times over the password
and a random per-user salt, so equal passwords get different hashes and every
guess costs the attacker the same work as a login.
- The stored string carries its parameters ("pbkdf2-sha256$iterations$salt$hash"),
  so verification uses the old ones and `needsRehash` can upgrade on next login.
- The password arrives as a CharArray (PBEKeySpec wants one) and is wiped after
  use; a String would stay in memory until the GC gets to it.
- The final comparison is constant-time. Argon2id or bcrypt are better choices
  where a library is allowed; the shape of the code stays the same.
*/

@JvmInline
value class Pbkdf2Iterations(val count: Int) {
    init {
        require(count >= 1) { "iterations must be positive" }
    }

    companion object {
        val RECOMMENDED = Pbkdf2Iterations(600_000) // OWASP guidance for PBKDF2-HMAC-SHA256
    }
}

object PasswordHasher {
    private const val PREFIX = "pbkdf2-sha256"
    private const val SALT_BYTES = 16
    private const val KEY_BITS = 256
    private val random = SecureRandom()
    private val encoder = Base64.getEncoder().withoutPadding()

    fun hash(password: CharArray, iterations: Pbkdf2Iterations = Pbkdf2Iterations.RECOMMENDED): String {
        val salt = ByteArray(SALT_BYTES).also(random::nextBytes)
        val derived = derive(password, salt, iterations)
        return listOf(PREFIX, iterations.count.toString(), encoder.encodeToString(salt), encoder.encodeToString(derived))
            .joinToString("$")
    }

    // Malformed stored values are a failed login, not an exception.
    fun verify(password: CharArray, stored: String): Boolean {
        val parts = stored.split('$')
        if (parts.size != 4 || parts[0] != PREFIX) return false
        val iterations = parts[1].toIntOrNull()?.takeIf { it >= 1 } ?: return false
        val salt = decodeOrNull(parts[2]) ?: return false
        val expected = decodeOrNull(parts[3]) ?: return false
        return MessageDigest.isEqual(expected, derive(password, salt, Pbkdf2Iterations(iterations)))
    }

    fun needsRehash(stored: String, current: Pbkdf2Iterations = Pbkdf2Iterations.RECOMMENDED): Boolean {
        val iterations = stored.split('$').getOrNull(1)?.toIntOrNull() ?: return true
        return iterations < current.count
    }

    private fun decodeOrNull(text: String): ByteArray? = runCatching { Base64.getDecoder().decode(text) }.getOrNull()

    private fun derive(password: CharArray, salt: ByteArray, iterations: Pbkdf2Iterations): ByteArray {
        val spec = PBEKeySpec(password, salt, iterations.count, KEY_BITS)
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded
        } finally {
            spec.clearPassword() // PBEKeySpec keeps its own copy of the characters
        }
    }
}

inline fun <T> CharArray.useThenWipe(block: (CharArray) -> T): T =
    try {
        block(this)
    } finally {
        fill('\u0000')
    }

fun main() {
    val demo = Pbkdf2Iterations(100_000) // lower than RECOMMENDED only to keep the demo quick
    val stored = "hunter2".toCharArray().useThenWipe { PasswordHasher.hash(it, demo) }
    println("stored: $stored")
    println("correct password: ${"hunter2".toCharArray().useThenWipe { PasswordHasher.verify(it, stored) }}")
    println("wrong password:   ${"hunter3".toCharArray().useThenWipe { PasswordHasher.verify(it, stored) }}")
    println("needs rehash: ${PasswordHasher.needsRehash(stored)}")
}
