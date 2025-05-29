package atoz.coroutines

import kotlin.io.path.createTempDirectory
import kotlin.io.path.writeText
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest

class DispatchersTest {
    @Test
    fun withContextReturnsTheResult() = runTest {
        assertEquals(333_833_500L, sumOfSquares(1_000))
        val file = createTempDirectory().resolve("app.conf").apply { writeText("port=8443") }
        assertEquals("port=8443", readConfig(file))
    }

    @Test
    fun withContextSwitchesThreadsAndComesBack() = runTest {
        val caller = Thread.currentThread().name
        assertTrue(threadNameOn(Dispatchers.Default).startsWith("DefaultDispatcher-worker"))
        namedDispatcher("audit-writer").use { assertEquals("audit-writer", threadNameOn(it)) }
        assertEquals(caller, Thread.currentThread().name)
    }
}
