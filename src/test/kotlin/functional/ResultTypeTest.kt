package atoz.functional

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertTrue

class ResultTypeTest {
    @Test
    fun successAndFailure() {
        assertEquals(PortSetting(8443), loadPortSetting(" 8443 ").getOrThrow())
        assertIs<NumberFormatException>(loadPortSetting("http").exceptionOrNull())
        assertIs<IllegalArgumentException>(loadPortSetting("70000").exceptionOrNull())
        assertTrue(loadPortSetting("70000").isFailure)
    }

    @Test
    fun unpackingWithDefaultsAndFold() {
        assertEquals(443, portOrDefault("443"))
        assertEquals(8080, portOrDefault("x"))
        assertEquals("listening on 443", describePortSetting("443"))
        assertEquals("invalid (IllegalArgumentException: port out of range: 0)", describePortSetting("0"))
    }

    @Test
    fun mapDoesNotCatchButMapCatchingDoes() {
        assertFailsWith<IllegalStateException> { parsePort("1").map { error("boom") } }
        assertTrue(parsePort("1").mapCatching { error("boom") }.isFailure)
    }

    @Test
    fun recoverChainsAlternatives() {
        val log = mutableListOf<String>()
        assertEquals(PortSetting(9000), firstWorkingPort(listOf("x", "0", "9000", "9001"), log).getOrThrow())
        assertEquals(listOf("skip 'x'", "skip '0'"), log)
    }
}
