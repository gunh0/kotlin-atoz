package atoz.modern

import kotlin.io.encoding.Base64
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class StableEncodingTest {
    private val bytes = byteArrayOf(-5, -17, 0, 42)

    @Test
    fun base64MatchesTheJdk() {
        assertEquals(java.util.Base64.getEncoder().encodeToString(bytes), Base64.Default.encode(bytes))
        assertEquals(java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(bytes), encodeToken(bytes))
        assertEquals("--8AKg", encodeToken(bytes))
        assertContentEquals(bytes, decodeToken("--8AKg"))
        assertFailsWith<IllegalArgumentException> { decodeToken("not base64!") }
    }

    @Test
    fun hexFormatting() {
        assertEquals("fbef002a", bytes.toHexString())
        assertEquals("FB:EF:00:2A", fingerprint(bytes))
        assertContentEquals(bytes, parseHex("fbef002a"))
        assertEquals("000000ff", 255.toHexString())
        assertEquals(java.util.HexFormat.of().formatHex(bytes), bytes.toHexString())
    }
}
