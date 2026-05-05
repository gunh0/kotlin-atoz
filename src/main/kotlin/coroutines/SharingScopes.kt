package atoz.coroutines

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.plus
import kotlinx.coroutines.runBlocking

/*
A cold flow runs its producer once per collector: three screens collecting a
polling flow means three polling loops against the backend. `shareIn` and
`stateIn` run the upstream once, inside a scope you provide, and multicast it:
- SharingStarted.Eagerly starts at once, Lazily on the first subscriber; both
  keep running until the scope is cancelled.
- WhileSubscribed(stopTimeoutMillis) stops the upstream after the last
  subscriber leaves (plus a grace period) and restarts it for the next one.
- `stateIn` needs an initial value and gives a StateFlow; `shareIn(replay = n)`
  gives a SharedFlow.
The scope owns the upstream: cancel it on shutdown and sharing stops. Sharing
in GlobalScope is a leak nobody can stop.
*/

class QuoteFeed(private val periodMs: Long = 1_000) {
    var starts = 0
        private set
    var stops = 0
        private set

    fun quotes(): Flow<Int> = flow {
        starts++
        var price = 0
        while (true) {
            emit(price++)
            delay(periodMs)
        }
    }.onCompletion { stops++ }
}

fun main() = runBlocking {
    val cold = QuoteFeed(periodMs = 10)
    repeat(3) { cold.quotes().take(2).toList() }
    println("cold flow, 3 collectors: upstream started ${cold.starts} times")

    val hot = QuoteFeed(periodMs = 10)
    val sharing = Job(coroutineContext[Job])
    val shared = hot.quotes().shareIn(this + sharing, SharingStarted.Lazily, replay = 1)
    repeat(3) { shared.first() }
    println("shared flow, 3 collectors: upstream started ${hot.starts} time(s)")
    sharing.cancel()
    sharing.join()
    println("after cancelling the sharing scope: stopped ${hot.stops} time(s)")
}
