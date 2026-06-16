package atoz.coroutines

import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit

/*
Bounding concurrency (a bulkhead) so one slow dependency can't absorb everything:
- `Semaphore(n).withPermit { }` lets at most n coroutines into the section at
  once; the rest suspend until a permit is free. Java's
  java.util.concurrent.Semaphore does the same but blocks a thread per waiter.
  Use it to cap in-flight calls, e.g. 3 concurrent requests to a fragile API.
- `dispatcher.limitedParallelism(n)` caps how many coroutines *run code* at the
  same time. A coroutine that suspends (delay, awaiting I/O) gives its slot
  back, so it does not cap in-flight requests at all. It is the right tool for
  CPU or blocking work: `Dispatchers.IO.limitedParallelism(4)` in front of a
  JDBC pool of 4 connections.
*/

class InFlightGauge {
    private val current = AtomicInteger()
    private val peak = AtomicInteger()

    val max: Int get() = peak.get()

    suspend fun <T> track(block: suspend () -> T): T {
        peak.accumulateAndGet(current.incrementAndGet(), ::maxOf)
        try {
            return block()
        } finally {
            current.decrementAndGet()
        }
    }
}

suspend fun callWithSemaphore(requests: Int, permits: Int, gauge: InFlightGauge) = coroutineScope {
    val semaphore = Semaphore(permits)
    repeat(requests) {
        launch { semaphore.withPermit { gauge.track { delay(100) } } }
    }
}

suspend fun callOnLimitedDispatcher(requests: Int, base: CoroutineDispatcher, parallelism: Int, gauge: InFlightGauge) =
    coroutineScope {
        val limited = base.limitedParallelism(parallelism)
        repeat(requests) {
            launch(limited) { gauge.track { delay(100) } } // suspends, so the slot is free again
        }
    }

suspend fun blockOnLimitedDispatcher(tasks: Int, parallelism: Int, gauge: InFlightGauge) = coroutineScope {
    val limited = Dispatchers.IO.limitedParallelism(parallelism)
    repeat(tasks) {
        launch(limited) { gauge.track { Thread.sleep(10) } } // blocking: holds the thread
    }
}

fun main() = runBlocking {
    val semaphore = InFlightGauge().also { callWithSemaphore(requests = 10, permits = 3, gauge = it) }
    println("semaphore(3), suspending calls: max in flight ${semaphore.max}")
    val limited = InFlightGauge().also { callOnLimitedDispatcher(10, Dispatchers.Default, parallelism = 2, gauge = it) }
    println("limitedParallelism(2), suspending calls: max in flight ${limited.max}")
    val blocking = InFlightGauge().also { blockOnLimitedDispatcher(tasks = 8, parallelism = 2, gauge = it) }
    println("limitedParallelism(2), blocking calls: max in flight ${blocking.max}")
}
