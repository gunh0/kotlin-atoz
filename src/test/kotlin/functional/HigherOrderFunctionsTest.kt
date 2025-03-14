package atoz.functional

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class HigherOrderFunctionsTest {
    @Test
    fun retryingRunsTheBlockUntilItSucceeds() {
        val failures = mutableListOf<Int>()
        val result = retrying(3, onFailure = { n, _ -> failures += n }) { attempt ->
            if (attempt < 3) error("timeout") else attempt
        }
        assertEquals(3, result)
        assertEquals(listOf(1, 2), failures)
    }

    @Test
    fun retryingGivesUpWithTheLastCause() {
        val e = assertFailsWith<IllegalStateException> { retrying(2) { error("down $it") } }
        assertEquals("down 2", e.cause?.message)
    }

    @Test
    fun functionsReturningFunctions() {
        assertEquals(42, multiplierOf(3)(14))
        assertEquals("result=42", (multiplierOf(3) andThen { "result=$it" })(14))
    }

    @Test
    fun ownFilter() {
        assertEquals(listOf(3, 6, 9), (1..10).toList().keepIf { it % 3 == 0 })
    }
}
