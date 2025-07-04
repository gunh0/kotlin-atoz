package atoz.coroutines

import java.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest

class FlowErrorsTest {
    @Test
    fun retryReplaysFromTheStart() = runTest {
        assertEquals(listOf(1, 1, 1, 2), withRetries(flakySensor(2), times = 3).toList())
        assertFailsWith<IOException> { withRetries(flakySensor(5), times = 1).toList() }
    }

    @Test
    fun catchEmitsAFallback() = runTest {
        val log = mutableListOf<String>()
        assertEquals(listOf(1, -1), withFallback(flakySensor(1), log).toList())
        assertEquals(listOf("caught read 1 failed", "completed, cause=null"), log)
    }

    @Test
    fun catchDoesNotSeeDownstreamExceptions() = runTest {
        val log = mutableListOf<String>()
        assertFailsWith<IllegalStateException> {
            withFallback(flakySensor(0), log).collect { check(it < 2) { "collector rejected $it" } }
        }
        assertEquals(1, log.size)
        assertEquals(true, log.single().startsWith("completed, cause=java.lang.IllegalStateException"))
    }
}
