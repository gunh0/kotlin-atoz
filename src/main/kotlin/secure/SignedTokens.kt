package atoz.secure

import java.security.MessageDigest
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset
import java.util.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/*
A stateless token: "subject.expiry.signature", where the signature is an
HMAC-SHA256 over the first two parts with a server-side key. Anyone can read
the payload, but nobody without the key can change it.
- Verify the signature first, in constant time, and only then parse and trust
  the payload. Checking expiry (or anything else) on unsigned data first turns
  the verifier into an oracle and invites parser bugs on attacker input.
- The expiry is inside the signed part, so extending it breaks the signature.
- Time comes from an injected java.time.Clock, so tests don't sleep.
- The result is a sealed type: callers must handle Expired vs BadSignature
  vs Malformed, and the subject is only reachable from Valid.
*/

sealed interface TokenCheck {
    data class Valid(val subject: String, val expiresAt: Instant) : TokenCheck
    data class Expired(val expiredAt: Instant) : TokenCheck
    data object BadSignature : TokenCheck
    data object Malformed : TokenCheck
}

class TokenSigner(key: ByteArray, private val clock: Clock = Clock.systemUTC()) {
    private val key = key.copyOf()
    private val encoder = Base64.getUrlEncoder().withoutPadding()
    private val decoder = Base64.getUrlDecoder()

    init {
        require(key.size >= 32) { "HMAC-SHA256 keys should have at least 256 bits" }
    }

    fun issue(subject: String, ttl: Duration): String {
        require(!ttl.isNegative && !ttl.isZero) { "ttl must be positive" }
        val expiresAt = clock.instant().plus(ttl).epochSecond
        val body = "${encoder.encodeToString(subject.toByteArray())}.$expiresAt"
        return "$body.${encoder.encodeToString(sign(body))}"
    }

    fun check(token: String): TokenCheck {
        val parts = token.split('.')
        if (parts.size != 3) return TokenCheck.Malformed
        val signature = runCatching { decoder.decode(parts[2]) }.getOrNull() ?: return TokenCheck.Malformed
        if (!MessageDigest.isEqual(sign("${parts[0]}.${parts[1]}"), signature)) return TokenCheck.BadSignature
        // From here on the payload was produced by us.
        val expiresAt = parts[1].toLongOrNull()?.let(Instant::ofEpochSecond) ?: return TokenCheck.Malformed
        if (!clock.instant().isBefore(expiresAt)) return TokenCheck.Expired(expiresAt)
        return TokenCheck.Valid(String(decoder.decode(parts[0])), expiresAt)
    }

    private fun sign(body: String): ByteArray = Mac.getInstance("HmacSHA256").run {
        init(SecretKeySpec(key, "HmacSHA256"))
        doFinal(body.toByteArray())
    }

    override fun toString() = "TokenSigner(key=****)"
}

fun main() {
    val key = ByteArray(32) { it.toByte() } // demo key; load a random one from a secret store in practice
    val issuedAt = Instant.parse("2026-04-28T09:00:00Z")
    val signer = TokenSigner(key, Clock.fixed(issuedAt, ZoneOffset.UTC))
    val token = signer.issue("alice", Duration.ofMinutes(15))
    println("token: $token")
    println("now: ${signer.check(token)}")
    val (subject, expiry, signature) = token.split('.')
    println("expiry pushed: ${signer.check("$subject.${expiry.toLong() + 3600}.$signature")}")
    val later = TokenSigner(key, Clock.fixed(issuedAt.plus(Duration.ofMinutes(16)), ZoneOffset.UTC))
    println("16 min later: ${later.check(token)}")
}
