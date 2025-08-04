package atoz.coroutines

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.test.runTest

class SharedStateTest {
    @Test
    fun mutexAndAtomicCountExactly() = runTest {
        assertEquals(10_000, countWithMutex(50, 200))
        assertEquals(10_000, countWithAtomic(50, 200))
    }

    @Test
    fun mutexIsNotReentrant() = runTest {
        val mutex = Mutex()
        mutex.withLock {
            assertTrue(mutex.isLocked)
            assertFalse(mutex.tryLock()) // a second lock() here would suspend forever
        }
        assertFalse(mutex.isLocked)
    }
}
