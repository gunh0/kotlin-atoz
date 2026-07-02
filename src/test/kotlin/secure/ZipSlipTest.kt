package atoz.secure

import java.io.ByteArrayInputStream
import java.nio.file.FileAlreadyExistsException
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.createTempDirectory
import kotlin.io.path.exists
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.readText
import kotlin.io.path.writeText
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ZipSlipTest {
    private val sandbox: Path = createTempDirectory("zipslip-test")
    private val target: Path = sandbox.resolve("target").createDirectories()

    private fun extract(vararg files: Pair<String, ByteArray>, maxEntries: Int = 100, maxTotalBytes: Long = 1_000_000) =
        SafeUnzip.extract(ByteArrayInputStream(zipOf(*files)), target, maxEntries, maxTotalBytes)

    @Test
    fun ordinaryArchivesExtractInsideTheTarget() {
        val files = extract("a.txt" to "A".toByteArray(), "dir/sub/b.txt" to "B".toByteArray(), "dir/./c.txt" to "C".toByteArray())
        assertEquals(listOf("a.txt", "dir/sub/b.txt", "dir/c.txt"), files.map { target.relativize(it).toString() })
        assertEquals("B", target.resolve("dir/sub/b.txt").readText())
    }

    @Test
    fun naiveExtractionWritesOutsideTheTarget() {
        naiveUnzip(ByteArrayInputStream(zipOf("../escaped.txt" to "pwned".toByteArray())), target)
        assertEquals("pwned", sandbox.resolve("escaped.txt").readText())
    }

    @Test
    fun escapingNamesAreRejected() {
        for (name in listOf("../escaped.txt", "a/../../escaped.txt", "/tmp/abs.txt", "..\\escaped.txt", "C:/x.txt", "..")) {
            assertFailsWith<UnsafeArchiveException>(name) { extract(name to "x".toByteArray()) }
        }
        assertFalse(sandbox.resolve("escaped.txt").exists())
        assertTrue(target.listDirectoryEntries().isEmpty())
    }

    @Test
    fun existingFilesAreNeverOverwritten() {
        target.resolve("config.txt").writeText("original")
        assertFailsWith<FileAlreadyExistsException> { extract("config.txt" to "replaced".toByteArray()) }
        assertEquals("original", target.resolve("config.txt").readText())
    }

    @Test
    fun zipBombLimitsCountActualBytes() {
        val zeros = ByteArray(1_000_000)
        assertTrue(zipOf("zeros.bin" to zeros).size < 5_000) // compresses about 1000:1
        val error = assertFailsWith<UnsafeArchiveException> { extract("zeros.bin" to zeros, maxTotalBytes = 64_000) }
        assertTrue("inflates" in error.message!!)
        val many = Array(20) { "f$it.txt" to byteArrayOf(1) }
        assertFailsWith<UnsafeArchiveException> { extract(*many, maxEntries = 10) }
    }
}
