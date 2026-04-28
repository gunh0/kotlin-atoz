package atoz.secure

import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset
import java.util.Base64
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SignedTokensTest {
    private val key = ByteArray(32) { (it * 7).toByte() }
    private val issuedAt = Instant.parse("2026-04-28T09:00:00Z")

    private fun signerAt(instant: Instant, signingKey: ByteArray = key) =
        TokenSigner(signingKey, Clock.fixed(instant, ZoneOffset.UTC))

    @Test
    fun roundTripKeepsTheSubject() {
        val token = signerAt(issuedAt).issue("alice.admin@例え", Duration.ofMinutes(15))
        assertEquals(3, token.split('.').size) // the subject is encoded, so dots in it are harmless
        assertEquals(
            TokenCheck.Valid("alice.admin@例え", issuedAt.plusSeconds(900)),
            signerAt(issuedAt.plusSeconds(60)).check(token),
        )
    }

    @Test
    fun expiryIsCheckedAgainstTheInjectedClock() {
        val token = signerAt(issuedAt).issue("alice", Duration.ofMinutes(15))
        val expiry = issuedAt.plusSeconds(900)
        assertEquals(TokenCheck.Valid("alice", expiry), signerAt(expiry.minusSeconds(1)).check(token))
        assertEquals(TokenCheck.Expired(expiry), signerAt(expiry).check(token))
    }

    @Test
    fun changingAnySignedPartBreaksTheSignature() {
        val token = signerAt(issuedAt).issue("alice", Duration.ofMinutes(15))
        val (subject, expiry, signature) = token.split('.')
        val bob = Base64.getUrlEncoder().withoutPadding().encodeToString("bob".toByteArray())
        assertEquals(TokenCheck.BadSignature, signerAt(issuedAt).check("$bob.$expiry.$signature"))
        assertEquals(TokenCheck.BadSignature, signerAt(issuedAt).check("$subject.${expiry.toLong() + 3600}.$signature"))
        assertEquals(TokenCheck.BadSignature, signerAt(issuedAt, ByteArray(32)).check(token))
    }

    @Test
    fun expiredButForgedTokensReportBadSignatureNotExpired() {
        val token = signerAt(issuedAt).issue("alice", Duration.ofMinutes(15))
        val (subject, expiry, _) = token.split('.')
        assertEquals(TokenCheck.BadSignature, signerAt(issuedAt.plusSeconds(86_400)).check("$subject.$expiry.AAAA"))
    }

    @Test
    fun malformedTokens() {
        for (token in listOf("", "abc", "a.b", "a.b.c.d", "a.b.!!!")) {
            assertEquals(TokenCheck.Malformed, signerAt(issuedAt).check(token), token)
        }
    }

    @Test
    fun weakKeysAndNonPositiveTtlsAreRejected() {
        assertFailsWith<IllegalArgumentException> { TokenSigner(ByteArray(16)) }
        assertFailsWith<IllegalArgumentException> { signerAt(issuedAt).issue("alice", Duration.ZERO) }
        assertEquals("TokenSigner(key=****)", signerAt(issuedAt).toString())
    }
}
