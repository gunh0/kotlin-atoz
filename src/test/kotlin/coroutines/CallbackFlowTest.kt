package atoz.coroutines

import java.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest

class CallbackFlowTest {
    @Test
    fun registersOnCollectAndUnregistersWhenDone() = runTest {
        val source = LoginEventSource()
        val received = async { source.events().take(2).toList() }
        testScheduler.runCurrent()
        assertEquals(1, source.listenerCount)
        source.publish("a")
        source.publish("b")
        source.publish("c")
        assertEquals(listOf("a", "b"), received.await())
        assertEquals(0, source.listenerCount)
    }

    @Test
    fun eachCollectorHasItsOwnListener() = runTest {
        val source = LoginEventSource()
        val first = async { source.events().take(1).toList() }
        val second = async { source.events().take(1).toList() }
        testScheduler.runCurrent()
        assertEquals(2, source.listenerCount)
        source.publish("x")
        assertEquals(listOf("x"), first.await())
        assertEquals(listOf("x"), second.await())
        assertEquals(0, source.listenerCount)
    }

    @Test
    fun closeWithErrorFailsTheCollectorAndStillCleansUp() = runTest {
        val source = LoginEventSource()
        val received = async { runCatching { source.events().toList() } }
        testScheduler.runCurrent()
        source.publish("a")
        source.fail(IOException("connection lost"))
        val error = received.await().exceptionOrNull()
        assertEquals(IOException::class, error!!::class)
        assertEquals(0, source.listenerCount)
    }

    @Test
    fun missingAwaitCloseIsAnErrorAndLeaksTheListener() = runTest {
        val source = LoginEventSource()
        assertFailsWith<IllegalStateException> { source.eventsWithoutAwaitClose().toList() }
        assertEquals(1, source.listenerCount)
    }
}
