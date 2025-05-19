package atoz.functional

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ExtensionsTest {
    @Test
    fun extensionFunctionsAndProperties() {
        assertEquals("i*******@example.com", "ian.park@example.com".maskedEmail())
        assertEquals("a@b", "a@b".maskedEmail())
        assertEquals(4, "  the quick  brown fox ".wordCount)
        assertEquals(2, listOf(1, 2, 3).secondOrNull())
        assertNull(listOf(1).secondOrNull())
    }

    @Test
    fun nullableReceiver() {
        val missing: String? = null
        assertEquals("-", missing.orDash())
        assertEquals("-", "  ".orDash())
        assertEquals("x", "x".orDash())
    }

    @Test
    fun extensionsAreResolvedStatically() {
        val circle = ExtCircle()
        assertEquals("circle", circle.kind())
        assertEquals("shape", kindOf(circle))
    }

    @Test
    fun membersWinOverExtensions() {
        assertEquals("member", ExtGreeter().hello())
    }
}
