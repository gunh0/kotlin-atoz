package atoz.oop

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ObjectsTest {
    @Test
    fun objectIsASingleton() {
        assertSame(SecurityDefaults, SecurityDefaults)
        assertTrue(SecurityDefaults.isAllowed("wss://chat"))
        assertFalse(SecurityDefaults.isAllowed("http://plain"))
    }

    @Test
    fun companionFactoriesValidate() {
        assertEquals(25.0, Temperature.ofCelsius(25.0).celsius, 1e-9)
        assertFailsWith<IllegalArgumentException> { Temperature.ofKelvin(-1.0) }
    }

    @Test
    fun companionCanImplementAnInterface() {
        val parsed = parseAll(Temperature, listOf("21.5C", "hot", "-300C"))
        assertEquals(1, parsed.size)
        assertEquals(21.5, parsed.single().celsius, 1e-9)
    }

    @Test
    fun objectExpression() {
        assertEquals(listOf("a", "aa", "bb", "ccc"), listOf("ccc", "a", "bb", "aa").sortedWith(byLengthThenAlpha))
    }
}
