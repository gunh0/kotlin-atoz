package atoz.basics

import java.util.Locale
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class StringCaseAndFormatTest {
    private val turkish = Locale.forLanguageTag("tr")

    @Test
    fun lowercaseIgnoresTheDefaultLocale() {
        val saved = Locale.getDefault()
        try {
            Locale.setDefault(turkish)
            assertEquals("title", "TITLE".lowercase())
            assertEquals("TITLE", "title".uppercase())
        } finally {
            Locale.setDefault(saved)
        }
        assertEquals("tıtle", "TITLE".lowercase(turkish))
        assertEquals("TİTLE", "title".uppercase(turkish))
    }

    @Test
    fun caseInsensitiveEqualityIsNotCanonicalization() {
        assertTrue("ADMİN".equals("admin", ignoreCase = true))
        assertNotEquals("admin", "ADMİN".lowercase())
        assertEquals(6, "ADMİN".lowercase().length) // i + combining dot above
        assertNull(canonicalUsername("ADMİN"))
        assertEquals("alice.ops", canonicalUsername(" Alice.Ops "))
        assertNull(canonicalUsername("al"))
    }

    @Test
    fun formattingNeedsAnExplicitLocale() {
        assertEquals("1,50", formatAmount(150, Locale.GERMANY))
        assertEquals("1.50", formatAmount(150))
    }

    @Test
    fun paddingAlignsButNeverTruncates() {
        assertEquals("alice...   1234.56", reportLine("alice", 123_456))
        assertEquals("bob.....      0.99", reportLine("bob", 99))
        assertEquals("verylongname", "verylongname".padEnd(4))
        assertEquals("007", "7".padStart(3, '0'))
    }
}
