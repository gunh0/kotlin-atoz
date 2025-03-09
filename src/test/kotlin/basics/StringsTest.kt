package atoz.basics

import kotlin.test.Test
import kotlin.test.assertEquals

class StringsTest {
    @Test
    fun templatesEvaluateExpressions() {
        assertEquals("ian writes 2 languages: Kotlin, Java", introduce("ian", listOf("Kotlin", "Java")))
        assertEquals("$5", priceTag(5))
    }

    @Test
    fun rawStringsKeepBackslashes() {
        assertEquals(11, windowsPath.length)
        assertEquals("C:\\temp\\new", windowsPath)
    }

    @Test
    fun trimIndentAndTrimMargin() {
        assertEquals("{\n  \"lang\": \"kotlin\",\n  \"price\": \"\$0\"\n}", jsonSnippet)
        assertEquals("first\n  second (indented)", marginText)
    }

    @Test
    fun buildStringQuotesWhenNeeded() {
        assertEquals("a,\"b,c\",\"say \"\"hi\"\"\"", csvLine(listOf("a", "b,c", "say \"hi\"")))
    }
}
