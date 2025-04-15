package atoz.secure

import kotlin.test.Test
import kotlin.test.assertEquals

class SealedResultsTest {
    @Test
    fun outcomesAreValuesNotExceptions() {
        val service = LoginService(mapOf("alice" to "pw"))
        assertEquals(LoginResult.Success("alice"), service.login("alice", "pw"))
        assertEquals(LoginResult.UnknownUser, service.login("bob", "pw"))
        assertEquals(LoginResult.WrongPassword, service.login("alice", "nope"))
    }

    @Test
    fun accountLocksAfterRepeatedFailures() {
        val service = LoginService(mapOf("alice" to "pw"), maxAttempts = 2)
        repeat(2) { service.login("alice", "nope") }
        assertEquals(LoginResult.Locked(2), service.login("alice", "pw")) // even the right password
    }

    @Test
    fun publicMessageDoesNotRevealWhichCheckFailed() {
        val service = LoginService(mapOf("alice" to "pw"))
        val unknown = service.login("bob", "x")
        val wrong = service.login("alice", "x")
        assertEquals(publicMessage(unknown), publicMessage(wrong))
        assertEquals("login failed reason=unknown-user", auditEntry("bob", unknown))
        assertEquals("login failed reason=wrong-password user=alice", auditEntry("alice", wrong))
    }
}
