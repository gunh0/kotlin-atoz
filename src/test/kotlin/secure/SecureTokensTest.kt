package atoz.secure

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class SecureTokensTest {
    @Test
    fun tokensAreUrlSafeAndLongEnough() {
        val token = newToken()
        assertEquals(43, token.length) // 32 bytes -> 43 base64 chars without padding
        assertTrue(token.all { it.isLetterOrDigit() || it == '-' || it == '_' })
        assertFailsWith<IllegalArgumentException> { newToken(8) }
    }

    @Test
    fun tokensDoNotRepeat() {
        assertEquals(1_000, List(1_000) { newToken() }.toSet().size)
    }

    @Test
    fun seededRandomIsReproducible() {
        assertEquals(predictableCodes(42, 5), predictableCodes(42, 5))
    }

    @Test
    fun secureCodesStayInRange() {
        repeat(100) { assertTrue(secureOneTimeCode() in 100_000..999_999) }
    }
}
