package atoz.oop

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class EnumClassesTest {
    @Test
    fun entriesKeepDeclarationOrder() {
        assertEquals(listOf("LOW", "MEDIUM", "HIGH", "CRITICAL"), Severity.entries.map { it.name })
        assertTrue(Severity.CRITICAL.atLeast(Severity.HIGH))
        assertFalse(Severity.LOW.atLeast(Severity.MEDIUM))
    }

    @Test
    fun parsingUntrustedInput() {
        assertEquals(Severity.CRITICAL, Severity.parse(" critical "))
        assertNull(Severity.parse("urgent"))
        assertFailsWith<IllegalArgumentException> { Severity.valueOf("urgent") }
    }

    @Test
    fun propertiesDriveLookups() {
        assertEquals(Severity.HIGH, Severity.fromScore(7.5))
        assertEquals(Severity.CRITICAL, Severity.fromScore(10.0))
        assertEquals(Severity.LOW, Severity.fromScore(0.3))
    }

    @Test
    fun perConstantOverrides() {
        assertEquals(listOf(80, 443, 22), Protocol.entries.map { it.defaultPort() })
        assertFalse(isEncrypted(Protocol.HTTP))
        assertTrue(isEncrypted(Protocol.SSH))
    }
}
