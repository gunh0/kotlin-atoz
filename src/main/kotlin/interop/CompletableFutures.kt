package atoz.interop

import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.future.asCompletableFuture
import kotlinx.coroutines.future.await
import kotlinx.coroutines.future.future
import kotlinx.coroutines.runBlocking

/*
Java async APIs (HttpClient.sendAsync, AWS SDK v2, many drivers) return
CompletableFuture / CompletionStage. The bridge to coroutines ships in
kotlinx-coroutines-core itself (package kotlinx.coroutines.future; it used to
be the separate -jdk8 artifact):
- `future.await()` suspends until the future completes, without blocking a
  thread, and rethrows the original exception: no ExecutionException or
  CompletionException wrapper to unpeel as with `get()` / `join()`.
- Cancelling the coroutine that awaits also cancels the future.
- The other direction: `scope.future { }` starts a coroutine and hands Java a
  CompletableFuture; `deferred.asCompletableFuture()` wraps an existing one.
  Cancelling that future from Java cancels the coroutine.
*/

// A Java-style API: the work runs on someone else's executor, the caller gets a future.
fun lookupRolesAsync(user: String, executor: Executor): CompletableFuture<List<String>> =
    CompletableFuture.supplyAsync({
        require(user.isNotBlank()) { "user must not be blank" }
        if (user == "root") listOf("admin", "audit") else listOf("reader")
    }, executor)

suspend fun isAdmin(user: String, executor: Executor): Boolean =
    "admin" in lookupRolesAsync(user, executor).await()

// For Java callers: same logic, exposed as a CompletableFuture.
fun CoroutineScope.riskScoreFuture(user: String): CompletableFuture<Int> = future {
    delay(10) // pretend to call a suspending scoring service
    if (user == "root") 90 else 10
}

fun main() = runBlocking {
    val executor = Executor { it.run() } // runs inline, enough for the demo
    println("root is admin: ${isAdmin("root", executor)}")
    println("blank user: ${runCatching { isAdmin(" ", executor) }.exceptionOrNull()}") // not a CompletionException
    val score = riskScoreFuture("root")
    println("Java sees ${score.javaClass.simpleName}, value ${score.await()}")
    val chained = async(Dispatchers.Default) { 6 * 7 }.asCompletableFuture().thenApply { it + 1 }
    println("thenApply on a Deferred: ${chained.await()}")
}
