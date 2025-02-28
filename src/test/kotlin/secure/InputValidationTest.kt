package atoz.secure

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class InputValidationTest {
    @Test
    fun validInputParses() {
        assertEquals(ListenAddress("api.internal", 8443), parseListenAddress("api.internal:8443"))
        assertEquals(ListenAddress("localhost", 80), listenAddressFromEnv { if (it == "ATOZ_LISTEN") "localhost:80" else null })
    }

    @Test
    fun badArgumentsAreIllegalArguments() {
        val missing = assertFailsWith<IllegalArgumentException> { listenAddressFromEnv { null } }
        assertEquals("listen address is not configured", missing.message)
        for (bad in listOf("api.internal", "api internal:80", "api:0", "api:99999", ":80", "a;rm -rf /:80")) {
            assertFailsWith<IllegalArgumentException>(bad) { parseListenAddress(bad) }
        }
    }

    @Test
    fun wrongStateIsIllegalState() {
        val account = Account(100)
        assertEquals(70, account.withdraw(30))
        assertFailsWith<IllegalArgumentException> { account.withdraw(0) }
        assertEquals("insufficient funds", assertFailsWith<IllegalStateException> { account.withdraw(500) }.message)
        account.close()
        assertEquals("account is closed", assertFailsWith<IllegalStateException> { account.withdraw(10) }.message)
    }
}
