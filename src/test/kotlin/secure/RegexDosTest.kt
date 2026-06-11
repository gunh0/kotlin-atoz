package atoz.secure

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RegexDosTest {
    private val ambiguous = Regex("^(.*,){8}x$")
    private val unambiguous = Regex("^(?:[^,]*,){8}x$")

    private fun commas(n: Int) = ",".repeat(n) + "!"

    @Test
    fun ambiguousRepetitionExplodes() {
        val at12 = BoundedRegex.stepsFor(ambiguous, commas(12), maxSteps = 1_000_000)
        val at16 = BoundedRegex.stepsFor(ambiguous, commas(16), maxSteps = 1_000_000)
        assertTrue(at16 > 5 * at12, "$at12 -> $at16") // 4 more characters, 5x+ the work
        assertFailsWith<RegexBudgetExceeded> { BoundedRegex.matches(ambiguous, commas(40)) }
    }

    @Test
    fun theUnambiguousRewriteMatchesTheSameAndStaysFlat() {
        assertTrue(BoundedRegex.stepsFor(unambiguous, commas(200)) < 50)
        assertFalse(BoundedRegex.matches(unambiguous, commas(40)))
        assertTrue(BoundedRegex.matches(unambiguous, "a,b,c,d,e,f,g,h,x"))
        assertTrue(BoundedRegex.matches(ambiguous, "a,b,c,d,e,f,g,h,x"))
    }

    @Test
    fun nestedQuantifiersAreQuadraticOnModernJdks() {
        val nested = Regex("^(a+)+$")
        val at100 = BoundedRegex.stepsFor(nested, "a".repeat(100) + "!")
        val at200 = BoundedRegex.stepsFor(nested, "a".repeat(200) + "!")
        assertTrue(at200 in 3 * at100..5 * at100, "$at100 -> $at200") // doubling the input ~quadruples the work
        assertFalse(BoundedRegex.matches(nested, "a".repeat(BoundedRegex.MAX_INPUT - 1) + "!")) // fine within the limit
    }

    @Test
    fun possessiveAndAtomicPatternsAreLinear() {
        val attack = "a".repeat(255) + "!"
        for (pattern in listOf("^a+$", "^(a++)+$", "^(?>a+)+$")) {
            assertTrue(BoundedRegex.stepsFor(Regex(pattern), attack) < 300, pattern)
            assertFalse(BoundedRegex.matches(Regex(pattern), attack), pattern)
            assertTrue(BoundedRegex.matches(Regex(pattern), "a".repeat(30)), pattern)
        }
    }

    @Test
    fun inputLengthIsBoundedBeforeMatching() {
        val error = assertFailsWith<IllegalArgumentException> {
            BoundedRegex.matches(Regex("^a+$"), "a".repeat(BoundedRegex.MAX_INPUT + 1))
        }
        assertEquals("input longer than 256 characters", error.message)
    }
}
