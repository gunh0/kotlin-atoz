package atoz.secure

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ConstantTimeCompareTest {
    @Test
    fun tokensMatchOnlyWhenIdentical() {
        assertTrue(tokensMatch("k_9f2c1e7a", "k_9f2c1e7a"))
        assertFalse(tokensMatch("k_9f2c1e7a", "k_9f2c1e7b"))
        assertFalse(tokensMatch("k_9f2c1e7a", "k_9f2c"))
        assertFalse(tokensMatch("k_9f2c1e7a", ""))
    }

    @Test
    fun handRolledVersionAgrees() {
        val samples = listOf("", "a", "ab", "abc", "abd", "xbc").map { it.toByteArray() }
        for (a in samples) for (b in samples) {
            assertEquals(a.contentEquals(b), constantTimeEquals(a, b))
        }
    }

    @Test
    fun hmacSignaturesAreFixedLengthAndVerified() {
        val key = "server-side-key".toByteArray()
        val signature = hmacSha256(key, "amount=100")
        assertEquals(32, signature.size)
        assertTrue(verifySignature(key, "amount=100", signature))
        assertFalse(verifySignature(key, "amount=900", signature))
        assertFalse(verifySignature("other-key".toByteArray(), "amount=100", signature))
    }
}
