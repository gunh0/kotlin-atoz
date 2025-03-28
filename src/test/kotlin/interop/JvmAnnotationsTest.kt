package atoz.interop

import java.lang.reflect.Modifier
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class JvmAnnotationsTest {
    private val generator = IdGenerator::class.java

    @Test
    fun jvmStaticAddsAStaticMethod() {
        assertTrue(Modifier.isStatic(generator.getMethod("next").modifiers))
        assertFalse(Modifier.isStatic(generator.getMethod("reset").modifiers))
        IdGenerator.reset()
        assertEquals("id-1", generator.getMethod("next").invoke(null)) // callable without INSTANCE
    }

    @Test
    fun jvmFieldAndConstExposeFields() {
        assertTrue(Modifier.isPublic(generator.getField("separator").modifiers))
        assertTrue(Modifier.isPublic(generator.getField("PREFIX").modifiers))
        assertFailsWith<NoSuchFieldException> { generator.getField("scope") }
        assertEquals("global", generator.getMethod("getScope").invoke(IdGenerator))
    }

    @Test
    fun jvmOverloadsGeneratesConstructors() {
        // ignoring the synthetic constructor Kotlin itself uses to fill in defaults
        val arities = Endpoint::class.java.constructors.filterNot { it.isSynthetic }.map { it.parameterCount }.sorted()
        assertEquals(listOf(1, 2, 3), arities)
        assertEquals("https://example.com:443", Endpoint("example.com").toString())
    }

    @Test
    fun jvmNameRenamesTheMethod() {
        val names = Class.forName("atoz.interop.JvmAnnotationsKt").declaredMethods.map { it.name }
        assertTrue("total" in names && "totalLengths" in names)
        assertEquals(6, total(listOf(1, 2, 3)))
        assertEquals(5, total(listOf("ab", "cde")))
    }
}
