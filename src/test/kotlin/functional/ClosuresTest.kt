package atoz.functional

import kotlin.test.Test
import kotlin.test.assertEquals

class ClosuresTest {
    @Test
    fun eachCounterHasItsOwnState() {
        val a = counter()
        val b = counter(100)
        assertEquals(listOf(1, 2, 3), List(3) { a() })
        assertEquals(101, b())
        assertEquals(4, a())
    }

    @Test
    fun capturedVarsCanBeModified() {
        assertEquals(10, sumWithForEach(listOf(1, 2, 3, 4)))
    }

    @Test
    fun lambdaSeesTheVariableNotACopy() {
        assertEquals("mode=dev" to "mode=prod", captureLatest())
    }

    @Test
    fun loopVariablesAreFreshPerIteration() {
        assertEquals(listOf(10, 20, 30), lambdasFromLoop().map { it() })
    }

    @Test
    fun closureOverAMutableMap() {
        val allow = rateLimiter(2)
        assertEquals(listOf(true, true, false), List(3) { allow("client-a") })
        assertEquals(true, allow("client-b"))
    }
}
