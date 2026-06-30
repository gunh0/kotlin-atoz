package atoz.basics

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CharsAndCodePointsTest {
    @Test
    fun lengthCountsUtf16CodeUnits() {
        assertEquals(2, "😀".length)
        assertEquals(1, "😀".codePointCount(0, 2))
        assertEquals(0x1F600, "😀".codePointAt(0))
        assertEquals(65, 'A'.code)
        assertEquals('A', Char(65))
    }

    @Test
    fun truncatingByCodePointKeepsPairsIntact() {
        assertTrue("hi👋".take(3).last().isHighSurrogate()) // broken half of a pair
        assertEquals("hi👋", truncateByCodePoints("hi👋 there", 3))
        assertEquals("short", truncateByCodePoints("short", 10))
    }

    @Test
    fun unicodeDigitsAreDigitsToo() {
        assertTrue('٣'.isDigit())
        assertEquals(3, '٣'.digitToInt())
        assertEquals(123, "١٢٣".toInt())
        assertNull(asciiNumberOrNull("١٢٣"))
        assertEquals(8443, asciiNumberOrNull("8443"))
        assertNull(asciiNumberOrNull("-1"))
        assertNull(asciiNumberOrNull("9999999999")) // would overflow Int
    }

    @Test
    fun nfkcFoldsLookalikes() {
        assertNotEquals("full", "ｆｕｌｌ")
        assertEquals("full", normalizedIdentifier("ｆｕｌｌ"))
        assertEquals("file", normalizedIdentifier("ﬁle"))
        assertEquals("admin", normalizedIdentifier("ＡＤＭＩＮ"))
    }
}
