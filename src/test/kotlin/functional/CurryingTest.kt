package atoz.functional

import kotlin.test.Test
import kotlin.test.assertEquals

class CurryingTest {
    @Test
    fun curriedFunctionTakesOneArgumentAtATime() {
        assertEquals("[ERROR] boom", logAt("ERROR")("boom"))
        assertEquals("[WARN] disk", warn("disk"))
        assertEquals(listOf("[DEBUG] a", "[DEBUG] b"), listOf("a", "b").map(logAt("DEBUG")))
    }

    @Test
    fun partialApplicationFixesTheFirstArgument() {
        assertEquals("https://api.example.com/v1/users/42", apiUrl("/users/42"))
        val times = { a: Int, b: Int -> a * b }
        assertEquals(21, times.partial(3)(7))
    }

    @Test
    fun uncurryingRoundTrips() {
        val add = { a: Int, b: Int -> a + b }
        assertEquals(5, add.curried().uncurried()(2, 3))
    }
}
