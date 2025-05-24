package atoz.basics

import kotlin.test.Test
import kotlin.test.assertEquals

class FunctionsTest {
    @Test
    fun defaultAndNamedArguments() {
        assertEquals("postgresql+tls://orders:5432/orders", connectionUrl("orders"))
        assertEquals("postgresql://orders:5432/orders", connectionUrl("orders", tls = false))
        assertEquals("postgresql+tls://replica:6432/orders", connectionUrl(host = "replica", database = "orders", port = 6432))
    }

    @Test
    fun singleExpressionFunction() {
        assertEquals(144, square(12))
    }

    @Test
    fun varargAndSpread() {
        assertEquals(0, sumAll())
        assertEquals(15, sumAll(1, 2, 3, *intArrayOf(4, 5)))
        assertEquals("usr/local", joinPath(parts = arrayOf("usr", "local")))
        assertEquals("a::b", joinPath("::", "a", "b"))
    }

    @Test
    fun localFunctions() {
        assertEquals(listOf(8080, 5432), validPorts(listOf(22, 8080, 70000, 443, 5432)))
    }
}
