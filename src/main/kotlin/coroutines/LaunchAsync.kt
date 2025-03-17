package atoz.coroutines

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/*
`launch` vs `async`: both start a child coroutine in a scope.
- `launch` is fire-and-forget and returns a Job you can `join()` or `cancel()`,
  roughly `executor.execute(runnable)`.
- `async` returns a Deferred<T>; `await()` suspends until the value is ready,
  roughly `executor.submit(callable).get()` without blocking a thread.
Starting all `async` calls first and awaiting afterwards runs them concurrently.
Awaiting each one right after starting it is just sequential code again.
`coroutineScope { }` (next unit) gives a suspend function a scope to start them in.
*/

suspend fun fetchPrice(item: String): Int {
    delay(100)
    return item.length * 10
}

suspend fun totalSequential(items: List<String>): Int = items.sumOf { fetchPrice(it) }

suspend fun totalConcurrent(items: List<String>): Int = coroutineScope {
    items.map { async { fetchPrice(it) } }.awaitAll().sum()
}

suspend fun launchAndJoin(log: MutableList<String>): Unit = coroutineScope {
    val job = launch {
        delay(50)
        log += "child done"
    }
    log += "launched" // runs first: launch returns immediately
    job.join()
    log += "joined"
}

fun main() = runBlocking {
    val items = listOf("pen", "book", "lamp")
    println("sequential total: ${totalSequential(items)}")
    println("concurrent total: ${totalConcurrent(items)}")
    val log = mutableListOf<String>()
    launchAndJoin(log)
    println(log)
}
