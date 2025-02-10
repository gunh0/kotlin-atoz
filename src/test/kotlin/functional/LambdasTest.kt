package atoz.functional

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LambdasTest {
    @Test
    fun lambdasAreValues() {
        assertEquals(5, add(2, 3))
        assertEquals(49, square(7))
        assertEquals(2, listOf("a", "  ", "", "b").count(isBlankLine))
        assertEquals("k", firstOfPair("k" to 1, 99))
    }

    @Test
    fun lastExpressionIsTheResult() {
        assertEquals("negative odd", classify(-3))
        assertEquals("non-negative even", classify(4))
    }

    @Test
    fun anonymousFunctionReturnsLocally() {
        assertEquals(listOf(0, 100, 255), listOf(-5, 100, 300).map(clampToByte))
    }

    @Test
    fun nullableFunctionTypes() {
        val received = mutableListOf<String>()
        assertTrue(notifyIfSet({ received += it }, "deployed"))
        assertFalse(notifyIfSet(null, "ignored"))
        assertEquals(listOf("deployed"), received)
    }
}
