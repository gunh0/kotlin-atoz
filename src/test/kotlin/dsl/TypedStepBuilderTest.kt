package atoz.dsl

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class TypedStepBuilderTest {
    @Test
    fun stepsInAnyOrderBuildTheSameRequest() {
        val a = request().url("https://api.example.com/x").method("GET").header("Accept", "application/json").build()
        val b = request().header("Accept", "application/json").method("GET").url("https://api.example.com/x").build()
        assertEquals("GET https://api.example.com/x {Accept=application/json}", a.toString())
        assertEquals(a.toString(), b.toString())
    }

    @Test
    fun draftsAreImmutableAndReusable() {
        val base = request().url("https://api.example.com/items")
        val get = base.method("GET").build()
        val delete = base.method("DELETE").header("X-Reason", "cleanup").build()
        assertEquals("GET", get.method)
        assertEquals(emptyMap(), get.headers)
        assertEquals("DELETE", delete.method)
        assertEquals(mapOf("X-Reason" to "cleanup"), delete.headers)
    }

    @Test
    fun phantomTypesExistOnlyAtCompileTime() {
        // Different static types, same runtime class: the state is erased.
        val missing: RequestDraft<Missing, Missing> = request()
        val present: RequestDraft<Present, Present> = request().url("https://a.example").method("GET")
        assertEquals<Class<*>>(missing.javaClass, present.javaClass)
    }

    @Test
    fun valuesAreStillCheckedAtRuntime() {
        assertFailsWith<IllegalArgumentException> { request().url("http://plain.example") }
        assertFailsWith<IllegalArgumentException> { request().method("TRACE") }
        assertFailsWith<IllegalArgumentException> { request().header("X", "a\r\nSet-Cookie: s=1") }
    }
}
