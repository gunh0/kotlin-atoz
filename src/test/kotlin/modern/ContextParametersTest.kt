package atoz.modern

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class ContextParametersTest {
    @Test
    fun contextComesFromWithBlock() {
        val trail = InMemoryAuditTrail()
        with(trail) { closeAccount("acc-1", 1_500) }
        assertEquals(listOf("transfer 1500 acc-1->payout", "closed acc-1"), trail.entries)
    }

    @Test
    fun contextFunctionAndForwardingThroughUnnamedContext() {
        val trail = InMemoryAuditTrail()
        context(trail) {
            closeAll(mapOf("a" to 0, "b" to 5))
        }
        assertEquals(listOf("rejected transfer a->payout", "closed a", "transfer 5 b->payout", "closed b"), trail.entries)
    }

    @Test
    fun contextIsNotThis() {
        val trail = InMemoryAuditTrail()
        val ok = with(trail) { transferFunds("x", "y", -1) }
        assertFalse(ok)
        assertEquals(1, trail.entries.size)
    }

    @Test
    fun compiledAsALeadingParameter() {
        val method = Class.forName("atoz.modern.ContextParametersKt").methods.single { it.name == "transferFunds" }
        assertEquals(
            listOf(AuditTrail::class.java, String::class.java, String::class.java, Long::class.javaPrimitiveType),
            method.parameterTypes.toList(),
        )
    }
}
