package atoz.coroutines

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest

class CancellationTest {
    @Test
    fun cancelOnlySetsAFlag() = runTest {
        var ignoring = -1
        var checking: Result<Int>? = null
        launch {
            coroutineContext.job.cancel()
            ignoring = busyLoop(1_000, cooperative = false)
            checking = runCatching { busyLoop(1_000, cooperative = true) }
        }.join()
        assertEquals(1_000, ignoring)
        assertTrue(checking!!.exceptionOrNull() is CancellationException)
    }

    @Test
    fun suspendingCleanupNeedsNonCancellable() = runTest {
        for ((protect, expected) in listOf(false to emptyList(), true to listOf("cleanup done"))) {
            val log = mutableListOf<String>()
            val job = launch { workWithCleanup(log, protect) }
            delay(100)
            job.cancelAndJoin()
            assertEquals(expected, log)
        }
    }
}
