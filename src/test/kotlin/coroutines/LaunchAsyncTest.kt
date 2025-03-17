package atoz.coroutines

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.test.runTest

class LaunchAsyncTest {
    private val items = listOf("pen", "book", "lamp")

    @Test
    fun sequentialCallsAddUp() = runTest {
        val clock = testScheduler.timeSource.markNow()
        assertEquals(110, totalSequential(items))
        assertEquals(300.milliseconds, clock.elapsedNow())
    }

    @Test
    fun asyncRunsConcurrently() = runTest {
        val clock = testScheduler.timeSource.markNow()
        assertEquals(110, totalConcurrent(items))
        assertEquals(100.milliseconds, clock.elapsedNow())
    }

    @Test
    fun launchReturnsImmediately() = runTest {
        val log = mutableListOf<String>()
        launchAndJoin(log)
        assertEquals(listOf("launched", "child done", "joined"), log)
    }
}
