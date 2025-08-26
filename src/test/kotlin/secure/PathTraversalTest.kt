package atoz.secure

import java.nio.file.Files
import kotlin.io.path.createDirectory
import kotlin.io.path.createTempDirectory
import kotlin.io.path.writeText
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class PathTraversalTest {
    private val tmp = createTempDirectory()
    private val base = tmp.resolve("files").createDirectory()
    private val dir = SafeDirectory(base)

    @Test
    fun namesInsideTheBaseResolve() {
        assertEquals(base.resolve("report.pdf"), dir.resolve("report.pdf"))
        assertEquals(base.resolve("report.pdf"), dir.resolve("img/../report.pdf"))
    }

    @Test
    fun traversalAndAbsolutePathsAreRejected() {
        for (bad in listOf("../secret.txt", "a/../../secret.txt", "/etc/passwd", "../files-evil/x")) {
            assertFailsWith<IllegalArgumentException>(bad) { dir.resolve(bad) }
        }
    }

    @Test
    fun stringPrefixCheckIsFooled() {
        assertTrue(naiveStringCheck(base.toString(), "../files-evil/x"))
    }

    @Test
    fun symlinkOutOfTheBaseIsRejected() {
        val outside = tmp.resolve("secret.txt").apply { writeText("s3cret") }
        Files.createSymbolicLink(base.resolve("link.txt"), outside)
        val e = assertFailsWith<IllegalArgumentException> { dir.resolve("link.txt") }
        assertEquals("path escapes the base directory via a link", e.message)
    }
}
