package atoz.functional

import java.util.function.Predicate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RuleCombinatorsTest {
    private fun request(user: String = "alice", roles: Set<String> = setOf("admin"), ip: String = "10.20.1.5", hour: Int = 10) =
        AccessRequest(user, roles, ip, hour)

    @Test
    fun combinatorsFollowBooleanLogic() {
        val even = Rule<Int> { it % 2 == 0 }
        val positive = Rule<Int> { it > 0 }
        assertEquals(listOf(true, false, false), listOf(4, 3, -2).map((even and positive)::allows))
        assertEquals(listOf(true, true, false), listOf(4, -2, -3).map((even or positive)::allows))
        assertEquals(listOf(false, true), listOf(4, 3).map((!even)::allows))
    }

    @Test
    fun auditLogPolicy() {
        val policy = AccessRules.readAuditLog
        assertTrue(policy.allows(request(hour = 23))) // admin on the office network at night
        assertTrue(policy.allows(request(user = "bob", roles = setOf("auditor"), ip = "203.0.113.9", hour = 11)))
        assertFalse(policy.allows(request(user = "bob", roles = setOf("auditor"), ip = "203.0.113.9", hour = 3)))
        assertFalse(policy.allows(request(user = "svc-backup"))) // service accounts never
        assertFalse(policy.allows(request(roles = setOf("developer"))))
    }

    @Test
    fun contravarianceAndEmptyCombinations() {
        val lockdown: Rule<AccessRequest> = AccessRules.denyAll
        assertFalse(lockdown.allows(request()))
        assertFalse(anyOf<AccessRequest>().allows(request()))
        assertFailsWith<IllegalArgumentException> { allOf<AccessRequest>() }
        assertTrue(allOf(AccessRules.isAdmin, AccessRules.fromOfficeNetwork).allows(request()))
    }

    @Test
    fun javaPredicateEquivalent() {
        val isAdmin = Predicate<AccessRequest> { "admin" in it.roles }
        val office = Predicate<AccessRequest> { it.ip.startsWith("10.20.") }
        val requests = listOf(request(), request(ip = "8.8.8.8"), request(roles = emptySet()))
        val kotlinRule = AccessRules.isAdmin and AccessRules.fromOfficeNetwork
        assertEquals(requests.map(isAdmin.and(office)::test), requests.map(kotlinRule::allows))
    }
}
