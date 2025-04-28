package atoz.oop

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class VisibilityTest {
    @Test
    fun subclassUsesProtectedState() {
        val plain = AuditedStore().apply { put("user", "ian") }
        assertEquals(listOf("put user=ian"), plain.auditTrail())

        val redacting = RedactingStore().apply { put("password", "hunter2") }
        assertEquals(listOf("put password=***"), redacting.auditTrail())
        assertEquals("hunter2", redacting.get("password"))
    }

    @Test
    fun internalIsVisibleWithinTheModule() {
        val store = AuditedStore().apply {
            put("a", "1")
            put("b", "2")
        }
        assertEquals(2, store.size())
    }

    @Test
    fun privateConstructorForcesValidation() {
        assertNull(ApiKey.parse("too-short"))
        val key = assertNotNull(ApiKey.parse("0123456789abcdef0123456789ABCDEF"))
        assertEquals("ApiKey(0123...)", key.toString())
    }
}
