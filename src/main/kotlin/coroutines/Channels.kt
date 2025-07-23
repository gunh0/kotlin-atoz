package atoz.coroutines

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.toList
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull

/*
Channels are coroutine queues for handing values between coroutines, like
java.util.concurrent.BlockingQueue but `send`/`receive` suspend instead of
blocking a thread. Capacity decides when `send` suspends:
- Channel() is a rendezvous channel: send waits until someone receives.
- Channel(n) buffers n elements; Channel.UNLIMITED never suspends the sender.
The producer calls `close()` when done; `for (x in channel)` then ends after
draining the remaining elements. Several consumers reading from one channel
share the work (fan-out): each element goes to exactly one of them.
*/

suspend fun processWithWorkers(items: List<String>, workers: Int): List<String> = coroutineScope {
    val jobs = Channel<String>(capacity = 2)
    val results = Channel<String>(Channel.UNLIMITED)
    launch {
        for (item in items) jobs.send(item) // suspends while the buffer is full
        jobs.close()
    }
    List(workers) { id ->
        launch { for (job in jobs) results.send("${job.uppercase()} by worker $id") }
    }.joinAll()
    results.close()
    results.toList()
}

suspend fun sendWithoutReceiver(capacity: Int): Boolean {
    val channel = Channel<Int>(capacity)
    return withTimeoutOrNull(100) { channel.send(1) } != null
}

fun main() = runBlocking {
    processWithWorkers(listOf("scan", "hash", "sign", "upload"), workers = 2).forEach(::println)
    println("rendezvous send completes alone: ${sendWithoutReceiver(Channel.RENDEZVOUS)}")
    println("buffered send completes alone: ${sendWithoutReceiver(1)}")
}
