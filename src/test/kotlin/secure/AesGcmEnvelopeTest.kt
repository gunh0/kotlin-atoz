package atoz.secure

import javax.crypto.AEADBadTagException
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse

class AesGcmEnvelopeTest {
    private val key = GcmEnvelope.newKey()
    private val aad = "user:42".toByteArray()
    private val secret = "card 4111 1111 1111 1111".toByteArray()

    @Test
    fun roundTripAndLayout() {
        val envelope = GcmEnvelope.seal(key, secret, aad)
        assertEquals(1 + 12 + secret.size + 16, envelope.size)
        assertEquals(1, envelope[0].toInt())
        assertContentEquals(secret, GcmEnvelope.open(key, envelope, aad))
    }

    @Test
    fun randomNoncesMakeEqualPlaintextsDiffer() {
        val first = GcmEnvelope.seal(key, secret, aad)
        val second = GcmEnvelope.seal(key, secret, aad)
        assertFalse(first.contentEquals(second))
        assertFalse(first.copyOfRange(1, 13).contentEquals(second.copyOfRange(1, 13)))
    }

    @Test
    fun anyModificationIsDetected() {
        val envelope = GcmEnvelope.seal(key, secret, aad)
        for (index in 1 until envelope.size) { // nonce, ciphertext and tag are all covered
            val tampered = envelope.copyOf().also { it[index] = (it[index].toInt() xor 1).toByte() }
            assertFailsWith<AEADBadTagException> { GcmEnvelope.open(key, tampered, aad) }
        }
    }

    @Test
    fun associatedDataBindsTheContext() {
        val envelope = GcmEnvelope.seal(key, secret, aad)
        assertFailsWith<AEADBadTagException> { GcmEnvelope.open(key, envelope, "user:43".toByteArray()) }
        assertFailsWith<AEADBadTagException> { GcmEnvelope.open(GcmEnvelope.newKey(), envelope, aad) }
    }

    @Test
    fun malformedEnvelopesAreRejectedBeforeDecryption() {
        assertFailsWith<IllegalArgumentException> { GcmEnvelope.open(key, ByteArray(20), aad) }
        val envelope = GcmEnvelope.seal(key, secret, aad).also { it[0] = 2 }
        assertFailsWith<IllegalArgumentException> { GcmEnvelope.open(key, envelope, aad) }
    }

    @Test
    fun bareAesIsEcbAndLeaksRepeatedBlocks() {
        val ciphertext = GcmEnvelope.ecbEncrypt(key, ByteArray(32))
        assertEquals(48, ciphertext.size) // two blocks plus a full PKCS5 padding block
        assertContentEquals(ciphertext.copyOfRange(0, 16), ciphertext.copyOfRange(16, 32))
        assertContentEquals(ciphertext, GcmEnvelope.ecbEncrypt(key, ByteArray(32))) // deterministic
    }
}
