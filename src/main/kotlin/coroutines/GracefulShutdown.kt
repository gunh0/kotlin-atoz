package atoz.coroutines

import java.util.Collections
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull

/*
Shutting down a worker pool without silently losing work, the coroutine
version of ExecutorService.shutdown() + awaitTermination() + shutdownNow():
1. Stop intake: `close()` the job channel. `submit` now fails with
   ClosedSendChannelException, while workers keep going: `for (job in channel)`
   drains the queued items before it ends.
2. Wait with a deadline: `withTimeoutOrNull(grace) { workers.joinAll() }`.
3. Past the deadline, cancel the workers. Cancellation lands at their next
   suspension point and `finally` blocks still run, so each worker can report
   the item it was interrupted on. Items still queued are drained with
   `tryReceive()` and returned, like the List<Runnable> of shutdownNow().
*/

data class ShutdownReport(val graceful: Boolean, val completed: List<String>, val interrupted: List<String>, val neverStarted: List<String>)

class WorkerPool(scope: CoroutineScope, workers: Int, private val handle: suspend (String) -> Unit) {
    private val queue = Channel<String>(Channel.UNLIMITED)
    private val completed = Collections.synchronizedList(mutableListOf<String>())
    private val interrupted = Collections.synchronizedList(mutableListOf<String>())

    private val jobs = List(workers) {
        scope.launch {
            for (item in queue) {
                var done = false
                try {
                    handle(item)
                    done = true
                } finally {
                    if (done) completed += item else interrupted += item
                }
            }
        }
    }

    suspend fun submit(item: String) = queue.send(item)

    suspend fun shutdown(grace: Duration): ShutdownReport {
        queue.close()
        val graceful = withTimeoutOrNull(grace) { jobs.joinAll() } != null
        if (!graceful) jobs.forEach { it.cancelAndJoin() }
        val neverStarted = generateSequence { queue.tryReceive().getOrNull() }.toList()
        return ShutdownReport(graceful, completed.toList(), interrupted.toList(), neverStarted)
    }
}

fun main() = runBlocking {
    val relaxed = WorkerPool(this, workers = 2) { delay(20) }
    listOf("a", "b", "c", "d").forEach { relaxed.submit(it) }
    println(relaxed.shutdown(grace = 1_000.milliseconds))

    val hurried = WorkerPool(this, workers = 2) { delay(20) }
    listOf("a", "b", "c", "d", "e", "f").forEach { hurried.submit(it) }
    println(hurried.shutdown(grace = 30.milliseconds))
}
