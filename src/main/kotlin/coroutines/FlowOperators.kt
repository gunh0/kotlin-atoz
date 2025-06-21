package atoz.coroutines

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.runBlocking

/*
Combining and pacing flows:
- `zip` pairs elements by position and stops with the shorter flow.
- `combine` emits whenever either side emits, using the latest value of each
  (think of a form whose fields change independently).
- By default producer and collector run in one coroutine, taking turns.
  `buffer()` runs the producer in its own coroutine so both work at once.
- `conflate()` also decouples them but keeps only the newest value, so a slow
  collector skips stale ones (a progress bar, a price ticker).
*/

fun ticks(count: Int, everyMs: Long): Flow<Int> = flow {
    for (i in 1..count) {
        delay(everyMs)
        emit(i)
    }
}

fun zipped(): Flow<String> = flowOf(1, 2, 3).zip(flowOf("a", "b")) { n, s -> "$n$s" }

fun combinedLatest(): Flow<String> =
    ticks(3, everyMs = 100).combine(ticks(2, everyMs = 160)) { a, b -> "$a/$b" }

suspend fun collectSlowly(flow: Flow<Int>, workMs: Long): List<Int> =
    flow.onEach { delay(workMs) }.toList()

fun main() = runBlocking {
    println("zip: ${zipped().toList()}")
    println("combine: ${combinedLatest().toList()}")
    val start = System.currentTimeMillis()
    println("buffered: ${collectSlowly(ticks(3, 100).buffer(), 300)} in ~${System.currentTimeMillis() - start} ms")
    println("conflated: ${collectSlowly(ticks(5, 100).conflate(), 250)}")
}
