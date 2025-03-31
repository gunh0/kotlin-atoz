package atoz.basics

import kotlin.test.Test
import kotlin.test.assertEquals

class ConditionalsTest {
    @Test
    fun ifIsAnExpression() {
        assertEquals(7, maxOfTwo(3, 7))
        assertEquals(3, maxOfTwo(3, -1))
    }

    @Test
    fun whenMatchesConstantsAndRanges() {
        assertEquals("success", statusText(204))
        assertEquals("redirect", statusText(302))
        assertEquals("denied", statusText(401))
        assertEquals("not found", statusText(404))
        assertEquals("server error", statusText(503))
        assertEquals("unexpected", statusText(999))
    }

    @Test
    fun whenWithoutSubjectTakesFirstTrueBranch() {
        assertEquals("weak", passwordStrength("a1!"))
        assertEquals("medium", passwordStrength("abcdefgh"))
        assertEquals("strong", passwordStrength("abcdefg1!"))
    }

    @Test
    fun whenCanBindItsSubject() {
        assertEquals("positive 42", describeInput("42"))
        assertEquals("zero", describeInput("0"))
        assertEquals("negative -5", describeInput("-5"))
        assertEquals("not a number: x", describeInput("x"))
    }
}
