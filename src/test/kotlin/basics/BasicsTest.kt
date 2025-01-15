package atoz.basics

import kotlin.test.Test
import kotlin.test.assertEquals

class BasicsTest {
    @Test
    fun greetingUsesDefaultName() {
        assertEquals("Hello, Kotlin!", greeting())
        assertEquals("Hello, Gopher!", greeting("Gopher"))
    }

    @Test
    fun variablesDescribeThemselves() {
        assertEquals(listOf("var num1: 10", "val num2: 20", "var num3: 1000"), Variable().describe())
    }
}
