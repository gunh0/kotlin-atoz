package atoz.coroutines

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlinx.coroutines.test.runTest

class ExceptionHandlingTest {
    @Test
    fun supervisorKeepsSiblingsAlive() = runTest {
        val log = mutableListOf<String>()
        supervisedTasks(log)
        assertEquals(listOf("handled: task A failed", "task B finished"), log)
    }

    @Test
    fun asyncFailureSurfacesAtAwait() = runTest {
        assertEquals("recovered: bad input", awaitInSupervisor())
    }

    @Test
    fun catchingAtAwaitIsNotEnoughInCoroutineScope() = runTest {
        val e = assertFailsWith<IllegalArgumentException> { awaitInCoroutineScope() }
        assertEquals("bad input", e.message)
    }
}
