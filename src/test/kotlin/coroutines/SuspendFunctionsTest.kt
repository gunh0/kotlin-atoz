package atoz.coroutines

import kotlin.coroutines.Continuation
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.test.runTest

class SuspendFunctionsTest {
    @Test
    fun suspendCallsRunSequentially() = runTest {
        val clock = testScheduler.timeSource.markNow()
        assertEquals("Hello, user-7", fetchGreeting(7))
        assertEquals(listOf("user-1", "user-2"), listOf(1, 2).map { fetchUserName(it) })
        assertEquals(300.milliseconds, clock.elapsedNow()) // three delays of 100 ms, one after another
    }

    @Test
    fun runBlockingBridgesFromRegularCode() {
        assertEquals("Hello, user-1", greetBlocking(1))
    }

    @Test
    fun compilerAddsAContinuationParameter() {
        val method = Class.forName("atoz.coroutines.SuspendFunctionsKt").methods.single { it.name == "fetchUserName" }
        assertEquals(listOf(Int::class.javaPrimitiveType, Continuation::class.java), method.parameterTypes.toList())
        assertEquals(Any::class.java, method.returnType)
    }
}
