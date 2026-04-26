package atoz.interop

import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionException
import java.util.concurrent.Executor
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.future.asCompletableFuture
import kotlinx.coroutines.future.await
import kotlinx.coroutines.future.future
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest

class CompletableFuturesTest {
    private val inline = Executor { it.run() }

    @Test
    fun awaitReturnsTheValue() = runTest {
        assertTrue(isAdmin("root", inline))
        assertFalse(isAdmin("alice", inline))
    }

    @Test
    fun awaitRethrowsTheOriginalException() = runTest {
        val future = lookupRolesAsync(" ", inline)
        // Java's join() wraps the failure ...
        assertFailsWith<CompletionException> { future.join() }
        // ... await() throws the exception the task threw
        assertFailsWith<IllegalArgumentException> { future.await() }
    }

    @Test
    fun cancellingTheAwaitingCoroutineCancelsTheFuture() = runTest {
        val pending = CompletableFuture<String>()
        val job = launch { pending.await() }
        testScheduler.runCurrent()
        job.cancel()
        job.join()
        assertTrue(pending.isCancelled)
    }

    @Test
    fun futureBuilderCompletesWithTheCoroutineResult() = runTest {
        assertEquals(90, backgroundScope.riskScoreFuture("root").await())
        assertEquals(10, backgroundScope.riskScoreFuture("alice").await())
    }

    @Test
    fun cancellingTheFutureFromJavaCancelsTheCoroutine() = runTest {
        val started = CompletableDeferred<Unit>()
        val cleanedUp = CompletableDeferred<Unit>()
        val future = backgroundScope.future {
            started.complete(Unit)
            try {
                awaitCancellation()
            } finally {
                cleanedUp.complete(Unit)
            }
        }
        started.await()
        future.cancel(true)
        cleanedUp.await()
        assertTrue(future.isCancelled)
    }

    @Test
    fun deferredAsCompletableFuture() = runTest {
        val deferred = async { 6 * 7 }
        assertEquals(43, deferred.asCompletableFuture().thenApply { it + 1 }.await())
    }
}
