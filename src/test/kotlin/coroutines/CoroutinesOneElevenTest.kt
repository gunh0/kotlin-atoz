package atoz.coroutines

import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.associateWith
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest

class CoroutinesOneElevenTest {
    private val attempts = flowOf(
        LoginAttempt("alice", "10.0.0.1", success = false),
        LoginAttempt("bob", "10.0.0.2", success = true),
        LoginAttempt("alice", "10.0.0.9", success = true),
    )

    @Test
    fun flowsCollectIntoMapsAndLaterKeysWin() = runTest {
        val byUser = lastAttemptByUser(attempts)
        assertEquals(listOf("alice", "bob"), byUser.keys.toList()) // first-seen order
        assertEquals("10.0.0.9", byUser.getValue("alice").ip)
        assertEquals(mapOf("alice" to "10.0.0.9", "bob" to "10.0.0.2"), lastIpByUser(attempts))
        assertEquals(mapOf("a" to 1, "bb" to 2), flowOf("a", "bb").associateWith { it.length })
    }

    @Test
    fun consumeToDrainsAndConsumesTheChannel() = runTest {
        val queue = Channel<String>(Channel.UNLIMITED)
        queue.send("one")
        queue.send("two")
        queue.close()
        assertEquals(listOf("one", "two"), drainAuditQueue(queue))
        assertTrue(queue.tryReceive().isClosed) // consumed: nothing more can be received
    }

    @Test
    fun onSubscriptionKeepsTheStateFlowType() = runTest {
        val subscriptions = AtomicInteger()
        val view: StateFlow<Int> = countingSubscriptions(MutableStateFlow(7), subscriptions)
        assertEquals(7, view.value) // still a StateFlow: no collection needed
        assertEquals(0, subscriptions.get())
        assertEquals(7, view.first())
        assertEquals(1, subscriptions.get())
    }
}
