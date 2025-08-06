package atoz.modern

import kotlin.test.Test
import kotlin.test.assertEquals

class NonLocalBreakContinueTest {
    @Test
    fun continueSkipsTheRestOfTheLineAndBreakStops() {
        // counted: one two | five | six
        assertEquals(4, countWords(listOf("one two # three four", "five", "six __END__ seven", "eight")))
        assertEquals(0, countWords(listOf("#all comment", "__END__ x")))
    }

    @Test
    fun headerParsingStopsAtTheBlankLine() {
        val headers = parseHeaders(listOf("Host: example.com", "# comment", "garbage", "X-Token: abc", "", "body: not a header"))
        assertEquals(mapOf("host" to "example.com", "x-token" to "abc"), headers)
    }
}
