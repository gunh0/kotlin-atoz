package atoz.functional

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class InlineFunctionsTest {
    @Test
    fun nonLocalReturnLeavesTheEnclosingFunction() {
        assertEquals(-4, firstNegative(listOf(3, 1, -4, -1)))
        assertNull(firstNegative(listOf(1, 2)))
    }

    @Test
    fun finallyStillRunsOnNonLocalReturn() {
        val log = mutableListOf<String>()
        assertEquals(1, firstNegativeIndex(listOf(5, -2, 7), log))
        assertEquals(listOf("start check 0", "end check 0", "start check 1", "end check 1"), log)
    }

    @Test
    fun noinlineLambdaCanBeStored() {
        val calls = mutableListOf<String>()
        val handlers = mutableListOf<() -> Unit>()
        registerHandlers(handlers, { calls += "now" }, { calls += "later" })
        assertEquals(listOf("now"), calls)
        handlers.single()()
        assertEquals(listOf("now", "later"), calls)
    }

    @Test
    fun crossinlineLambdaRunsInsideAnotherContext() {
        var ran = false
        asTask { ran = true }.run()
        assertTrue(ran)
    }
}
