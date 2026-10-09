package atoz.secure

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AuthorizationContextTest {
    private val roles = mapOf("root" to setOf("admin", "user"), "eve" to setOf("user"))

    @Test
    fun onlyAdminsGetTheCapability() {
        assertNull(VerifiedAdmin.authorize("eve", roles))
        assertNull(VerifiedAdmin.authorize("mallory", roles))
        assertEquals("root", VerifiedAdmin.authorize("root", roles)!!.userId)
        // No public constructor to forge one. (The accessor Kotlin generates for the companion is synthetic,
        // and javac refuses to call synthetic members.)
        assertTrue(VerifiedAdmin::class.java.constructors.all { it.isSynthetic })
    }

    @Test
    fun deniedRequestsNeverRunThePrivilegedBlock() {
        val directory = AccountDirectory(listOf("root", "bob", "eve"))
        var ran = false
        assertNull(asAdmin("eve", roles) { ran = true; directory.disable("bob") })
        assertEquals(false, ran)
        assertEquals(setOf("root", "bob", "eve"), directory.activeUsers)
    }

    @Test
    fun theCapabilityFlowsThroughHelpersIntoTheAuditLog() {
        val directory = AccountDirectory(listOf("root", "alice", "bob"))
        asAdmin("root", roles) { directory.disableAll(listOf("alice", "bob", "nobody")) }
        assertEquals(setOf("root"), directory.activeUsers)
        assertEquals(listOf("root: disabled alice", "root: disabled bob"), directory.auditLog)
    }

    @Test
    fun theContextValueIsUsableLikeAParameter() {
        val directory = AccountDirectory(listOf("root"))
        assertFailsWith<IllegalArgumentException> { asAdmin("root", roles) { directory.disable("root") } }
        assertEquals("root: x", asAdmin("root", roles) { auditEntryFor("x") })
    }

    @Test
    fun onTheJvmTheContextIsALeadingParameter() {
        val disable = AccountDirectory::class.java.methods.single { it.name == "disable" }
        assertEquals(listOf(VerifiedAdmin::class.java, String::class.java), disable.parameterTypes.toList())
        val entry = Class.forName("atoz.secure.AuthorizationContextKt").methods.single { it.name == "auditEntryFor" }
        assertEquals(listOf(VerifiedAdmin::class.java, String::class.java), entry.parameterTypes.toList())
    }
}
