package atoz.coroutines

import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeTo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.associate
import kotlinx.coroutines.flow.associateBy
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.runBlocking

/*
Small stable additions in kotlinx.coroutines 1.11.0:
- Flow terminal operators that collect into a Map, mirroring the collection
  functions: `associate`, `associateBy`, `associateWith` (and `*To`). As with
  lists, a later element with the same key wins; iteration follows the flow.
- `ReceiveChannel.consumeTo(collection)` drains a channel into a collection and
  consumes it (the channel is cancelled afterwards, as with `consumeEach`).
- `StateFlow.onSubscription { }` now returns a StateFlow, so `.value` stays
  available (before, it returned a plain SharedFlow).
(`CompletableDeferred.asDeferred` and `SharedFlow.asFlow` also arrived, but are
still @ExperimentalCoroutinesApi.)
*/

data class LoginAttempt(val user: String, val ip: String, val success: Boolean)

suspend fun lastAttemptByUser(attempts: Flow<LoginAttempt>): Map<String, LoginAttempt> =
    attempts.associateBy { it.user }

suspend fun lastIpByUser(attempts: Flow<LoginAttempt>): Map<String, String> =
    attempts.associate { it.user to it.ip }

suspend fun drainAuditQueue(queue: ReceiveChannel<String>): List<String> = queue.consumeTo(mutableListOf())

fun countingSubscriptions(state: StateFlow<Int>, subscriptions: AtomicInteger): StateFlow<Int> =
    state.onSubscription { subscriptions.incrementAndGet() }

fun main() = runBlocking {
    val attempts = flowOf(
        LoginAttempt("alice", "10.0.0.1", success = false),
        LoginAttempt("bob", "10.0.0.2", success = true),
        LoginAttempt("alice", "10.0.0.9", success = true),
    )
    println("last attempt per user: ${lastAttemptByUser(attempts)}")
    println("last ip per user: ${lastIpByUser(attempts)}")
    val queue = Channel<String>(Channel.UNLIMITED)
    listOf("login alice", "logout alice").forEach { queue.send(it) }
    queue.close()
    println("drained: ${drainAuditQueue(queue)}")
}
