package atoz.modern

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NestedTypeAliasesTest {
    @Test
    fun nestedAliasIsReferencedThroughItsClass() {
        val rule: AccessPolicy.Rule = { user, _ -> user == "root" }
        val policy = AccessPolicy(mapOf("delete" to rule))
        assertTrue(policy.isAllowed("root", "delete"))
        assertFalse(policy.isAllowed("alice", "delete"))
        assertEquals("no rule for export: deny", policy.explain("root", "export"))
    }

    @Test
    fun aliasIsJustAnotherNameForTheSameType() {
        val plain: (String, String) -> Boolean = { _, action -> action == "read" }
        val asRule: AccessPolicy.Rule = plain // no conversion needed
        val table: AccessPolicy.RuleTable = mapOf("read" to plain)
        assertTrue(asRule("anyone", "read"))
        assertTrue(AccessPolicy(table).isAllowed("anyone", "read"))
    }

    @Test
    fun sameAliasNameInTwoClassesDoesNotClash() {
        val passwordRule: PasswordPolicy.Rule = { it.length > 3 }
        assertTrue(passwordRule("abcd"))
        assertTrue(PasswordPolicy.accepts("correct horse 42"))
        assertFalse(PasswordPolicy.accepts("short1"))
    }

    @Test
    fun jvmSignatureShowsTheExpandedType() {
        val constructor = AccessPolicy::class.java.constructors.single()
        assertEquals(Map::class.java, constructor.parameterTypes.single())
    }
}
