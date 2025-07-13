package atoz.coroutines

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield

/*
Hot flows exist independently of collectors:
- StateFlow always has a current `value`, emits only distinct values and is
  conflated: a slow collector sees the latest state, not every step.
  It is the coroutine version of an observable property.
- SharedFlow broadcasts events to every active collector, with an optional
  `replay` cache for late subscribers. Events are not conflated or deduplicated.
Expose the read-only type (`asStateFlow()`, `asSharedFlow()`) and keep the
mutable one private, as you would keep a Java field private behind a getter.
`update { }` is an atomic compare-and-set loop, safe from many threads.
*/

class LoginCounter {
    private val _failures = MutableStateFlow(0)
    val failures: StateFlow<Int> = _failures.asStateFlow()

    fun recordFailure() = _failures.update { it + 1 }
    fun reset() {
        _failures.value = 0
    }
}

class AuditBus(replay: Int) {
    private val _events = MutableSharedFlow<String>(replay = replay)
    val events: SharedFlow<String> = _events.asSharedFlow()

    suspend fun publish(event: String) = _events.emit(event)
}

fun main() = runBlocking {
    val counter = LoginCounter()
    val seen = launch { println("observed: ${counter.failures.take(2).toList()}") }
    yield() // let the collector subscribe; it gets the current value 0
    repeat(3) { counter.recordFailure() } // no suspension in between: conflated to 3
    seen.join()

    val bus = AuditBus(replay = 1)
    bus.publish("login alice")
    println("late subscriber sees: ${bus.events.take(1).toList()}")
}
