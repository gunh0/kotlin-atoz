package atoz.coroutines

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.yield

class StateAndSharedFlowTest {
    @Test
    fun stateFlowHoldsAValueAndConflates() = runTest {
        val counter = LoginCounter()
        val seen = async { counter.failures.take(2).toList() }
        yield()
        repeat(3) { counter.recordFailure() }
        assertEquals(listOf(0, 3), seen.await())
        assertEquals(3, counter.failures.value)
    }

    @Test
    fun stateFlowSkipsEqualValues() = runTest {
        val counter = LoginCounter()
        val seen = mutableListOf<Int>()
        val job = launch { counter.failures.collect { seen += it } }
        yield()
        counter.reset() // already 0: not emitted again
        yield()
        counter.recordFailure()
        yield()
        job.cancel()
        assertEquals(listOf(0, 1), seen)
    }

    @Test
    fun sharedFlowBroadcastsAndReplays() = runTest {
        val bus = AuditBus(replay = 1)
        bus.publish("old")
        val first = async { bus.events.take(3).toList() }
        val second = async { bus.events.take(3).toList() }
        yield()
        bus.publish("a")
        bus.publish("b")
        assertEquals(listOf("old", "a", "b"), first.await())
        assertEquals(listOf("old", "a", "b"), second.await())
    }
}
