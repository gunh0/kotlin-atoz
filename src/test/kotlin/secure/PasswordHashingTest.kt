package atoz.secure

import java.util.Base64
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class PasswordHashingTest {
    private val fast = Pbkdf2Iterations(1_000) // tests only; production uses RECOMMENDED

    @Test
    fun storedFormatCarriesItsParameters() {
        val parts = PasswordHasher.hash("hunter2".toCharArray(), fast).split('$')
        assertEquals(4, parts.size)
        assertEquals("pbkdf2-sha256", parts[0])
        assertEquals("1000", parts[1])
        assertEquals(16, Base64.getDecoder().decode(parts[2]).size)
        assertEquals(32, Base64.getDecoder().decode(parts[3]).size)
    }

    @Test
    fun saltMakesEqualPasswordsHashDifferently() {
        val first = PasswordHasher.hash("hunter2".toCharArray(), fast)
        val second = PasswordHasher.hash("hunter2".toCharArray(), fast)
        assertNotEquals(first, second)
        assertTrue(PasswordHasher.verify("hunter2".toCharArray(), first))
        assertTrue(PasswordHasher.verify("hunter2".toCharArray(), second))
        assertFalse(PasswordHasher.verify("hunter3".toCharArray(), first))
    }

    @Test
    fun matchesTheKnownPbkdf2Sha256Vector() {
        // PBKDF2-HMAC-SHA256, P="password", S="salt", c=1, dkLen=32
        val expected = "120fb6cffcf8b32c43e7225256c4f837a86548c92ccc35480805987cb70be17b".hexToByteArray()
        val encoder = Base64.getEncoder().withoutPadding()
        val stored = listOf("pbkdf2-sha256", "1", encoder.encodeToString("salt".toByteArray()), encoder.encodeToString(expected))
            .joinToString("$")
        assertTrue(PasswordHasher.verify("password".toCharArray(), stored))
    }

    @Test
    fun malformedStoredValuesFailClosed() {
        val malformed = listOf(
            listOf("plain"),
            listOf("md5", "1", "c2FsdA", "aGFzaA"),
            listOf("pbkdf2-sha256", "0", "c2FsdA", "aGFzaA"),
            listOf("pbkdf2-sha256", "x", "c2FsdA", "aGFzaA"),
            listOf("pbkdf2-sha256", "1", "!!", "aGFzaA"),
            listOf("pbkdf2-sha256", "1", "c2FsdA", "aGFzaA", "extra"),
        ).map { it.joinToString("$") } + ""
        for (stored in malformed) {
            assertFalse(PasswordHasher.verify("password".toCharArray(), stored), stored)
        }
    }

    @Test
    fun outdatedParametersNeedARehash() {
        val stored = PasswordHasher.hash("hunter2".toCharArray(), fast)
        assertTrue(PasswordHasher.needsRehash(stored))
        assertFalse(PasswordHasher.needsRehash(stored, current = fast))
        assertTrue(PasswordHasher.needsRehash("garbage"))
        assertFailsWith<IllegalArgumentException> { Pbkdf2Iterations(0) }
    }

    @Test
    fun passwordCharsAreWipedAfterUse() {
        val password = "hunter2".toCharArray()
        val stored = password.useThenWipe { PasswordHasher.hash(it, fast) }
        assertTrue(password.all { it == '\u0000' })
        assertTrue(PasswordHasher.verify("hunter2".toCharArray(), stored))
    }
}
