package atoz.basics

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class NothingAndUnitTest {
    @Test
    fun unitIsASingleValue() {
        val sink = mutableListOf<String>()
        assertSame(Unit, appendLine(sink, "x"))
        assertEquals(listOf("x"), sink)
        val actions: List<() -> Unit> = listOf({ sink += "y" })
        actions.forEach { it() }
        assertEquals(listOf("x", "y"), sink)
    }

    @Test
    fun nothingFitsWhereAnyTypeIsExpected() {
        assertEquals(22, lookupPort(mapOf("ssh" to 22), "ssh"))
        val e = assertFailsWith<IllegalStateException> { lookupPort(emptyMap(), "ftp") }
        assertEquals("unknown service: ftp", e.message)
    }

    @Test
    fun todoThrowsNotImplementedError() {
        assertFailsWith<NotImplementedError> { notYetImplemented() }
    }

    @Test
    fun listOfNothingIsAListOfAnything() {
        assertEquals(emptyList(), rolesOf(false))
        assertEquals(listOf("admin"), rolesOf(true))
    }
}
