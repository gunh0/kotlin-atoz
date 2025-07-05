package atoz.modern

import kotlin.test.Test
import kotlin.test.assertEquals

class WhenGuardsTest {
    @Test
    fun guardedBranchWinsWhenItsConditionHolds() {
        assertEquals("alert: brute force on bob", triage(SecurityEvent.LoginFailed("bob", 7)))
        assertEquals("alert: /etc/sudoers changed", triage(SecurityEvent.FileWrite("/etc/sudoers", byAdmin = false)))
    }

    @Test
    fun otherwiseTheNextMatchingBranchApplies() {
        assertEquals("info: failed login for alice", triage(SecurityEvent.LoginFailed("alice", 1)))
        assertEquals("info: wrote /etc/hosts", triage(SecurityEvent.FileWrite("/etc/hosts", byAdmin = true)))
        assertEquals("info: logout", triage(SecurityEvent.Logout))
    }

    @Test
    fun elseIfIsAGuardedFallback() {
        assertEquals("warn: large download", triage(SecurityEvent.Download(5_000_000_000)))
        assertEquals("info: Download", triage(SecurityEvent.Download(10)))
    }
}
