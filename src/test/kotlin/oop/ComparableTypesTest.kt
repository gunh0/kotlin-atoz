package atoz.oop

import java.math.BigDecimal
import java.util.TreeSet
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ComparableTypesTest {
    private fun v(text: String) = SemVer.parse(text)

    @Test
    fun ordersNumericallyFieldByField() {
        assertTrue(v("1.10.0") > v("1.9.3"))
        assertFalse("1.10.0" > "1.9.3") // string order gets this wrong
        assertTrue(v("2.0.0-rc1") < v("2.0.0"))
        assertTrue(v("2.0.0-alpha") < v("2.0.0-beta"))
        assertEquals(
            listOf("1.4.0", "1.9.3", "1.10.0", "2.0.0-rc1", "2.0.0"),
            listOf("2.0.0", "1.10.0", "1.9.3", "2.0.0-rc1", "1.4.0").map(::v).sorted().map(SemVer::toString),
        )
    }

    @Test
    fun rangesAndHelpersWorkForAnyComparable() {
        assertTrue(isSupportedClient("1.4.0"))
        assertTrue(isSupportedClient("2.0.0-rc1"))
        assertFalse(isSupportedClient("1.3.9"))
        assertFalse(isSupportedClient("2.0.1"))
        assertEquals(v("2.0.0"), v("3.1.0").coerceIn(supportedClients))
        assertEquals(v("1.10.0"), maxOf(v("1.9.3"), v("1.10.0")))
    }

    @Test
    fun compareToIsConsistentWithEquals() {
        val a = v("1.2.3")
        val b = SemVer(1, 2, 3)
        assertEquals(0, a.compareTo(b))
        assertEquals(a, b)
        assertEquals(setOf(a, b).size, TreeSet(listOf(a, b)).size)
    }

    @Test
    fun bigDecimalIsTheCounterExample() {
        val one = BigDecimal("1.0")
        val oneHundredths = BigDecimal("1.00")
        assertEquals(0, one.compareTo(oneHundredths))
        assertFalse(one == oneHundredths)
        assertEquals(2, hashSetOf(one, oneHundredths).size)
        assertEquals(1, TreeSet(listOf(one, oneHundredths)).size)
    }

    @Test
    fun parseRejectsGarbage() {
        assertFailsWith<IllegalArgumentException> { v("1.2") }
        assertFailsWith<IllegalArgumentException> { v("1.2.3-rc1; rm -rf /") }
    }
}
