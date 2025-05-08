package atoz.interop

import java.util.concurrent.Callable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SamConversionsTest {
    @Test
    fun samConstructorsKeepDefaultMethods() {
        assertEquals(listOf("c", "go", "java", "kotlin"), listOf("kotlin", "go", "java", "c").sortedWith(byLengthThenAlphabet))
        assertEquals(listOf("kotlin", "java", "go", "c"), listOf("kotlin", "go", "java", "c").sortedWith(byLengthThenAlphabet.reversed()))
        assertTrue(notBlank.test("x"))
        assertFalse(notBlank.test(" "))
        assertEquals("KOTLIN-JAVA", joinWith.andThen { it.uppercase() }.apply("kotlin", "java"))
    }

    @Test
    fun lambdasPassedToJavaMethods() {
        assertEquals(listOf("a", "b"), removeBlank(mutableListOf("a", " ", "b", "")))
        assertEquals(mapOf('a' to listOf("apple", "avocado"), 'b' to listOf("banana")), groupByInitial(listOf("apple", "avocado", "banana")))
        assertEquals(listOf("first", "second"), runOnExecutor(listOf(Callable { "first" }, Callable { "second" })))
        val ran = mutableListOf<String>()
        Thread { ran += "runnable" }.apply { start(); join() }
        assertEquals(listOf("runnable"), ran)
    }

    @Test
    fun funInterfaceEnablesSamForKotlin() {
        assertEquals("padded", trimSanitizer.clean("  padded  "))
    }
}
