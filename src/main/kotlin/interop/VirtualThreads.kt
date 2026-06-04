package atoz.interop

import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/*
JDK 21 virtual threads from Kotlin. `Executors.newVirtualThreadPerTaskExecutor()`
starts a cheap virtual thread per task; `asCoroutineDispatcher()` turns it into
a dispatcher for *blocking* code you can't make suspending (JDBC, legacy SDKs,
Thread.sleep-style APIs): a blocked virtual thread parks and frees its carrier
OS thread.
- Suspending APIs don't block anyway; virtual threads add nothing there.
- A fixed pool (Dispatchers.IO: 64 threads by default) can deadlock when more
  tasks than threads block waiting for each other; one thread per task can't.
- Close the dispatcher when done; it shuts the executor down.
- On JDK 21 blocking inside `synchronized` pins the carrier thread (fixed in
  JDK 24, JEP 491); use ReentrantLock in such code.
*/

fun virtualThreadDispatcher(): ExecutorCoroutineDispatcher =
    Executors.newVirtualThreadPerTaskExecutor().asCoroutineDispatcher()

suspend fun runsOnVirtualThread(dispatcher: CoroutineDispatcher): Boolean =
    withContext(dispatcher) { Thread.currentThread().isVirtual }

// Every task blocks until all tasks have arrived: needs `tasks` threads at once.
suspend fun allReachBarrier(dispatcher: CoroutineDispatcher, tasks: Int, timeoutMs: Long): Boolean {
    val barrier = CountDownLatch(tasks)
    return withContext(dispatcher) {
        List(tasks) {
            async {
                barrier.countDown()
                barrier.await(timeoutMs, TimeUnit.MILLISECONDS)
            }
        }.awaitAll().all { it }
    }
}

fun main() = runBlocking {
    virtualThreadDispatcher().use { virtual ->
        println("virtual: ${runsOnVirtualThread(virtual)}, Default: ${runsOnVirtualThread(Dispatchers.Default)}")
        println("10 000 blocking tasks meet on virtual threads: ${allReachBarrier(virtual, tasks = 10_000, timeoutMs = 5_000)}")
    }
    println("16 tasks on 8 pooled threads: ${allReachBarrier(Dispatchers.IO.limitedParallelism(8), tasks = 16, timeoutMs = 100)}")
}
