package atoz.coroutines

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.test.runTest

class StructuredConcurrencyTest {
    @Test
    fun scopeWaitsForAllChildren() = runTest {
        val clock = testScheduler.timeSource.markNow()
        val log = mutableListOf<String>()
        assertEquals(listOf("profile", "alerts"), loadDashboard(log))
        assertEquals(listOf("audit written"), log)
        assertEquals(300.milliseconds, clock.elapsedNow())
    }

    @Test
    fun failureCancelsSiblingsAndPropagates() = runTest {
        val clock = testScheduler.timeSource.markNow()
        val log = mutableListOf<String>()
        val e = assertFailsWith<IllegalStateException> { failingSiblings(log) }
        assertEquals("fast failed", e.message)
        assertEquals(listOf("slow cancelled"), log)
        assertEquals(100.milliseconds, clock.elapsedNow()) // did not wait for the slow child
    }
}
