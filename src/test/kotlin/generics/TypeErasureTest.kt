package atoz.generics

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TypeErasureTest {
    @Test
    fun typeArgumentsAreErasedAtRuntime() {
        assertTrue(sameRuntimeClass())
        assertTrue(isListOfSomething(listOf(1)))
        assertFalse(isListOfSomething(setOf(1)))
    }

    @Test
    fun uncheckedCastFailsLaterAtTheUseSite() {
        val polluted = pretendStrings(listOf(1, 2))
        assertEquals(2, polluted.size)
        assertFailsWith<ClassCastException> { firstLength(polluted) }
    }

    @Test
    fun typeOfKeepsTypeArguments() {
        assertTrue(sameType<List<String>, List<String>>())
        assertFalse(sameType<List<String>, List<Int>>())
        assertEquals(listOf(String::class, List::class), typeArgumentClasses<Map<String, List<Int>>>())
        assertTrue(isNullableType<String?>())
        assertFalse(isNullableType<String>())
    }
}
