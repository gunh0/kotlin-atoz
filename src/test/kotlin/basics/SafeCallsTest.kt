package atoz.basics

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SafeCallsTest {
    private val full = Profile("ian", ProfileAddress("seoul", " 04524 "))

    @Test
    fun chainStopsAtFirstNull() {
        assertEquals("SEOUL", cityLabel(full))
        assertEquals("UNKNOWN", cityLabel(Profile("x", null)))
        assertEquals("UNKNOWN", cityLabel(Profile("x", ProfileAddress(null, null))))
        assertEquals("UNKNOWN", cityLabel(null))
    }

    @Test
    fun letRunsOnlyForNonNull() {
        assertEquals("Welcome back, ian", greetingFor(full))
        assertEquals("Welcome, guest", greetingFor(null))
    }

    @Test
    fun takeIfJoinsValidationToTheChain() {
        assertEquals("04524", validZip(full))
        assertNull(validZip(Profile("x", ProfileAddress("busan", "4852"))))
    }

    @Test
    fun mapNotNullDropsFailures() {
        assertEquals(listOf(80, 443), parsePorts(listOf("80", "http", "443", "70000")))
    }
}
