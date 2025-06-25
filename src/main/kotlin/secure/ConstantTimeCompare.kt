package atoz.secure

import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/*
`==` on Strings (String.equals in Java) stops at the first differing
character, so the time a comparison takes reveals how much of a guessed
token was right. Over many requests an attacker can recover a secret API key
or HMAC signature byte by byte.
Compare secrets with MessageDigest.isEqual, which looks at every byte
regardless of where they differ (it only returns early on a length mismatch,
and length is rarely secret). Comparing fixed-length digests (HMAC, SHA-256)
instead of the raw values also hides the length.
Kotlin offers nothing special here; this is the same JDK call as in Java.
*/

fun tokensMatch(expected: String, provided: String): Boolean =
    MessageDigest.isEqual(expected.toByteArray(), provided.toByteArray())

// What isEqual does: accumulate differences with XOR/OR instead of returning early.
fun constantTimeEquals(a: ByteArray, b: ByteArray): Boolean {
    if (a.size != b.size) return false
    var diff = 0
    for (i in a.indices) diff = diff or (a[i].toInt() xor b[i].toInt())
    return diff == 0
}

fun hmacSha256(key: ByteArray, message: String): ByteArray =
    Mac.getInstance("HmacSHA256").run {
        init(SecretKeySpec(key, "HmacSHA256"))
        doFinal(message.toByteArray())
    }

fun verifySignature(key: ByteArray, message: String, signature: ByteArray): Boolean =
    MessageDigest.isEqual(hmacSha256(key, message), signature)

fun main() {
    val apiKey = "k_9f2c1e7a"
    println("exact: ${tokensMatch(apiKey, "k_9f2c1e7a")}, prefix: ${tokensMatch(apiKey, "k_9f2c")}")
    val key = "server-side-key".toByteArray()
    val signature = hmacSha256(key, "amount=100")
    println("valid signature: ${verifySignature(key, "amount=100", signature)}")
    println("tampered message: ${verifySignature(key, "amount=900", signature)}")
}
