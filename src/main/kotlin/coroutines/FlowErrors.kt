package atoz.coroutines

import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking

/*
Errors in flows are handled declaratively:
- `catch { }` handles exceptions from upstream only and may emit a fallback.
  It never sees exceptions thrown downstream in `collect`: that is the
  "exception transparency" rule, which also forbids wrapping `emit` in try/catch.
- `retry(n) { cause -> }` re-collects the upstream flow from the beginning,
  so values emitted before the failure are emitted again.
- `onCompletion { cause -> }` runs like `finally`, with the cause (or null).
Java's Stream has no equivalent; checked exceptions in lambdas usually end up
wrapped in RuntimeException by hand.
*/

fun flakySensor(failuresBeforeSuccess: Int): Flow<Int> {
    var attempts = 0
    return flow {
        attempts++
        emit(1)
        if (attempts <= failuresBeforeSuccess) throw IOException("read $attempts failed")
        emit(2)
    }
}

fun withRetries(source: Flow<Int>, times: Long): Flow<Int> = source.retry(times) { it is IOException }

fun withFallback(source: Flow<Int>, log: MutableList<String>): Flow<Int> =
    source
        .catch { e ->
            log += "caught ${e.message}"
            emit(-1)
        }
        .onCompletion { cause -> log += "completed, cause=$cause" }

fun main() = runBlocking {
    println("retried: ${withRetries(flakySensor(2), times = 3).toList()}")
    val log = mutableListOf<String>()
    println("fallback: ${withFallback(flakySensor(1), log).toList()} $log")
}
