package atoz.interop

import java.io.IOException
import java.nio.file.NoSuchFileException
import java.nio.file.Path
import kotlin.io.path.createTempDirectory
import kotlin.io.path.writeText
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class CheckedExceptionsTest {
    private val dir = createTempDirectory()

    @Test
    fun javaCheckedExceptionsNeedNoTryCatch() {
        val file = dir.resolve("a.txt").apply { writeText("first\nsecond") }
        assertEquals("first", readFirstLine(file))
        assertFailsWith<NoSuchFileException> { readFirstLine(dir.resolve("missing.txt")) }
        val empty = dir.resolve("empty.txt").apply { writeText("") }
        assertFailsWith<IOException> { readFirstLineUndeclared(empty) }
        assertNull(firstLineOrNull(empty))
    }

    @Test
    fun throwsAnnotationAddsTheThrowsClause() {
        val facade = Class.forName("atoz.interop.CheckedExceptionsKt")
        val declared = facade.getMethod("readFirstLine", Path::class.java).exceptionTypes.toList()
        val undeclared = facade.getMethod("readFirstLineUndeclared", Path::class.java).exceptionTypes.toList()
        assertEquals(listOf(IOException::class.java), declared)
        assertEquals(emptyList(), undeclared)
    }
}
