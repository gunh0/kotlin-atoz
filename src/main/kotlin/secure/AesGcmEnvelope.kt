package atoz.secure

import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/*
AES-GCM is authenticated encryption: it hides the data *and* detects any
change to it. Decryption of a modified envelope throws AEADBadTagException
instead of returning garbage. Rules that matter more than the algorithm name:
- Never reuse a nonce with the same key; a random 96-bit nonce per message is
  fine for up to about 2^32 messages per key.
- Associated data (AAD) is authenticated but not encrypted. Bind the context
  (record id, tenant, purpose) so a ciphertext copied to another row fails.
- `Cipher.getInstance("AES")` in the JDK means AES/ECB/PKCS5Padding: no IV, no
  integrity, and equal plaintext blocks give equal ciphertext blocks.
Envelope layout: version byte | 12-byte nonce | ciphertext with 16-byte tag.
*/

object GcmEnvelope {
    private const val VERSION: Byte = 1
    private const val NONCE_BYTES = 12
    private const val TAG_BITS = 128
    private val random = SecureRandom()

    fun newKey(): SecretKey = KeyGenerator.getInstance("AES").apply { init(256) }.generateKey()

    fun seal(key: SecretKey, plaintext: ByteArray, associatedData: ByteArray): ByteArray {
        val nonce = ByteArray(NONCE_BYTES).also(random::nextBytes)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(TAG_BITS, nonce))
        cipher.updateAAD(associatedData)
        return byteArrayOf(VERSION) + nonce + cipher.doFinal(plaintext)
    }

    fun open(key: SecretKey, envelope: ByteArray, associatedData: ByteArray): ByteArray {
        require(envelope.size >= 1 + NONCE_BYTES + TAG_BITS / 8) { "envelope too short" }
        require(envelope[0] == VERSION) { "unknown envelope version" }
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(TAG_BITS, envelope, 1, NONCE_BYTES))
        cipher.updateAAD(associatedData)
        return cipher.doFinal(envelope, 1 + NONCE_BYTES, envelope.size - 1 - NONCE_BYTES)
    }

    // The anti-example: what the bare "AES" transformation does.
    fun ecbEncrypt(key: SecretKey, plaintext: ByteArray): ByteArray =
        Cipher.getInstance("AES").apply { init(Cipher.ENCRYPT_MODE, key) }.doFinal(plaintext)
}

fun main() {
    val key = GcmEnvelope.newKey()
    val aad = "user:42/field:iban".toByteArray()
    val envelope = GcmEnvelope.seal(key, "DE89 3704 0044 0532 0130 00".toByteArray(), aad)
    println("envelope: ${envelope.size} bytes, opened: ${GcmEnvelope.open(key, envelope, aad).decodeToString()}")
    val moved = runCatching { GcmEnvelope.open(key, envelope, "user:43/field:iban".toByteArray()) }
    println("copied to another user: ${moved.exceptionOrNull()?.javaClass?.simpleName}")
    envelope[20] = (envelope[20].toInt() xor 1).toByte()
    println("one bit flipped: ${runCatching { GcmEnvelope.open(key, envelope, aad) }.exceptionOrNull()?.javaClass?.simpleName}")
    val ecb = GcmEnvelope.ecbEncrypt(key, ByteArray(32))
    println("ECB blocks equal: ${ecb.copyOfRange(0, 16).contentEquals(ecb.copyOfRange(16, 32))}")
}
