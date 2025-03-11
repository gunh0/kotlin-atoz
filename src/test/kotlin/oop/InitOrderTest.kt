package atoz.oop

import kotlin.test.Test
import kotlin.test.assertEquals

class InitOrderTest {
    @Test
    fun initializersRunInDeclarationOrder() {
        val log = mutableListOf<String>()
        InitTrace("x", log)
        assertEquals(listOf("first property (x)", "first init", "second property", "second init"), log)
    }

    @Test
    fun secondaryConstructorBodyRunsLast() {
        val log = mutableListOf<String>()
        InitTrace(log)
        assertEquals("first property (anonymous)", log.first())
        assertEquals("secondary constructor body", log.last())
    }

    @Test
    fun openCallFromBaseSeesUninitializedState() {
        val log = mutableListOf<String>()
        InitDerived(log)
        assertEquals(listOf("base init sees: label=null", "derived init sees: label=derived"), log)
    }
}
