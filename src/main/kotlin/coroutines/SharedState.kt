package atoz.coroutines

import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/*
Shared mutable state needs the same care as with Java threads once coroutines
run on a multi-threaded dispatcher: `count++` from many coroutines loses updates.
- Mutex.withLock { } is the coroutine version of `synchronized`: waiting for the
  lock suspends instead of blocking a thread. (A suspend call inside a
  `synchronized` block does not even compile.)
- Mutex is not reentrant: locking it again from inside `withLock` deadlocks.
- For a single counter, java.util.concurrent atomics are simpler and faster.
*/

suspend fun countUnsafely(coroutines: Int, increments: Int): Int {
    var count = 0
    withContext(Dispatchers.Default) {
        repeat(coroutines) { launch { repeat(increments) { count++ } } }
    }
    return count
}

suspend fun countWithMutex(coroutines: Int, increments: Int): Int {
    val mutex = Mutex()
    var count = 0
    withContext(Dispatchers.Default) {
        repeat(coroutines) { launch { repeat(increments) { mutex.withLock { count++ } } } }
    }
    return count
}

suspend fun countWithAtomic(coroutines: Int, increments: Int): Int {
    val count = AtomicInteger()
    withContext(Dispatchers.Default) {
        repeat(coroutines) { launch { repeat(increments) { count.incrementAndGet() } } }
    }
    return count.get()
}

fun main() = runBlocking {
    println("expected 100000")
    println("unsafe: ${countUnsafely(100, 1_000)}") // often less, depending on cores and timing
    println("mutex:  ${countWithMutex(100, 1_000)}")
    println("atomic: ${countWithAtomic(100, 1_000)}")
}
