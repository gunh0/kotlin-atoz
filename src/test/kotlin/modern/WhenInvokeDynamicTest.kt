package atoz.modern

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class WhenInvokeDynamicTest {
    @Test
    fun typeWhenCompilesToTypeSwitch() {
        assertTrue(usesTypeSwitch(CredentialRouting::class.java))
        assertFalse(usesTypeSwitch(Credential.Password::class.java)) // a class without a type `when`
    }

    @Test
    fun semanticsAreUnchanged() {
        assertEquals("password", CredentialRouting.kind(Credential.Password("h")))
        assertEquals("api key ak_test", CredentialRouting.kind(Credential.ApiKey("ak_test")))
        assertEquals("passkey", CredentialRouting.kind(Credential.Passkey("p")))
    }

    @Test
    fun firstMatchingBranchStillWins() {
        assertEquals("none", CredentialRouting.failureCategory(null))
        assertEquals("auth", CredentialRouting.failureCategory(LockedOut("x"))) // the later `is LockedOut` branch is unreachable
        assertEquals("auth", CredentialRouting.failureCategory(AuthFailure("x")))
        assertEquals("bad request", CredentialRouting.failureCategory(NumberFormatException()))
        assertEquals("internal", CredentialRouting.failureCategory(IllegalStateException()))
    }
}
