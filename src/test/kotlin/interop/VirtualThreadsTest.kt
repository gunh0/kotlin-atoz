package atoz.interop

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class VirtualThreadsTest {
    @Test
    fun dispatcherRunsCoroutinesOnVirtualThreads() = runBlocking {
        virtualThreadDispatcher().use { assertTrue(runsOnVirtualThread(it)) }
        assertFalse(runsOnVirtualThread(Dispatchers.Default))
        assertFalse(runsOnVirtualThread(Dispatchers.IO))
    }

    @Test
    fun everyBlockingTaskGetsItsOwnThread() = runBlocking {
        virtualThreadDispatcher().use { assertTrue(allReachBarrier(it, tasks = 2_000, timeoutMs = 5_000)) }
    }

    @Test
    fun aFixedPoolCannotHoldMoreBlockedTasksThanThreads() = runBlocking {
        assertFalse(allReachBarrier(Dispatchers.IO.limitedParallelism(4), tasks = 8, timeoutMs = 100))
    }
}
