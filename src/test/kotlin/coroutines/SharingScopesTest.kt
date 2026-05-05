package atoz.coroutines

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest

class SharingScopesTest {
    @Test
    fun coldFlowRunsOncePerCollector() = runTest {
        val feed = QuoteFeed()
        assertEquals(listOf(0, 1), feed.quotes().take(2).toList())
        assertEquals(listOf(0, 1), feed.quotes().take(2).toList())
        assertEquals(2, feed.starts)
    }

    @Test
    fun shareInRunsTheUpstreamOnceForAllCollectors() = runTest {
        val feed = QuoteFeed()
        val shared = feed.quotes().shareIn(backgroundScope, SharingStarted.Lazily, replay = 1)
        val a = async { shared.take(3).toList() }
        val b = async { shared.take(3).toList() }
        assertEquals(listOf(0, 1, 2), a.await())
        assertEquals(listOf(0, 1, 2), b.await())
        assertEquals(1, feed.starts)
    }

    @Test
    fun whileSubscribedStopsAfterTheGracePeriodAndRestarts() = runTest {
        val feed = QuoteFeed(periodMs = 1_000)
        val state = feed.quotes().stateIn(backgroundScope, SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000), initialValue = -1)
        assertEquals(-1, state.value) // nothing started yet
        assertEquals(1, state.first { it == 1 }) // subscriber leaves at t = 1 s
        assertEquals(1, feed.starts)

        testScheduler.advanceTimeBy(4_000.milliseconds)
        testScheduler.runCurrent()
        assertEquals(0, feed.stops, "stops at 5 s") // still inside the 5 s grace period
        testScheduler.advanceTimeBy(2_000.milliseconds)
        assertEquals(1, feed.stops, "stops at 7 s")
        assertTrue(state.value >= 5, "value ${state.value}") // the last value is kept

        assertEquals(0, state.first { it == 0 }) // a new subscriber restarts the upstream
        assertEquals(2, feed.starts, "starts after resubscribe")
    }

    @Test
    fun cancellingTheScopeStopsEagerSharing() = runTest {
        val feed = QuoteFeed()
        val scope = CoroutineScope(coroutineContext + Job())
        feed.quotes().shareIn(scope, SharingStarted.Eagerly)
        testScheduler.runCurrent()
        assertEquals(1, feed.starts) // started without any subscriber
        scope.cancel()
        testScheduler.runCurrent()
        assertEquals(1, feed.stops)
    }
}
