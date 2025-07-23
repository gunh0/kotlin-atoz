package atoz.coroutines

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.test.runTest

class ChannelsTest {
    @Test
    fun everyItemIsProcessedExactlyOnce() = runTest {
        val items = (1..20).map { "item$it" }
        val results = processWithWorkers(items, workers = 3)
        assertEquals(items.map { it.uppercase() }.sorted(), results.map { it.substringBefore(" by") }.sorted())
        assertEquals(3, results.map { it.substringAfter("worker ") }.toSet().size)
    }

    @Test
    fun capacityDecidesWhenSendSuspends() = runTest {
        assertFalse(sendWithoutReceiver(Channel.RENDEZVOUS))
        assertTrue(sendWithoutReceiver(1))
    }
}
