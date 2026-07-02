package atoz.secure

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import kotlin.io.path.createDirectories
import kotlin.io.path.createTempDirectory
import kotlin.io.path.exists
import kotlin.io.path.readText

/*
An archive entry name is attacker input. ZipInputStream hands you
"../../home/app/.ssh/authorized_keys" or "/etc/cron.d/x" unchanged, and
`target.resolve(entry.name)` happily points outside the target ("Zip Slip").
- Resolve, `normalize()`, and require the result to stay strictly inside the
  target (the Path check from PathTraversal); reject absolute names and
  backslashes, which are separators on Windows.
- Create files with CREATE_NEW: never overwrite, never write through an
  existing symlink.
- Zip bombs: a few KB can inflate to gigabytes. Limit the entry count and the
  bytes actually written; the size in the entry header is attacker data too.
*/

class UnsafeArchiveException(message: String) : RuntimeException(message)

object SafeUnzip {
    fun extract(zip: InputStream, target: Path, maxEntries: Int = 1_000, maxTotalBytes: Long = 50L * 1024 * 1024): List<Path> {
        val root = target.toAbsolutePath().normalize()
        val written = mutableListOf<Path>()
        var totalBytes = 0L
        ZipInputStream(zip).use { entries ->
            while (true) {
                val entry = entries.nextEntry ?: break
                if (written.size >= maxEntries) throw UnsafeArchiveException("too many entries")
                val destination = destinationFor(root, entry.name)
                if (entry.isDirectory) {
                    destination.createDirectories()
                    continue
                }
                destination.parent.createDirectories()
                Files.newOutputStream(destination, StandardOpenOption.CREATE_NEW).use { out ->
                    val buffer = ByteArray(8_192)
                    while (true) {
                        val read = entries.read(buffer)
                        if (read < 0) break
                        totalBytes += read
                        if (totalBytes > maxTotalBytes) throw UnsafeArchiveException("archive inflates beyond $maxTotalBytes bytes")
                        out.write(buffer, 0, read)
                    }
                }
                written.add(destination) // not `+=`: a Path is itself an Iterable<Path>
            }
        }
        return written
    }

    private fun destinationFor(root: Path, name: String): Path {
        if (name.isEmpty() || name.startsWith("/") || '\\' in name || ':' in name) {
            throw UnsafeArchiveException("illegal entry name")
        }
        val destination = root.resolve(name).normalize()
        if (!destination.startsWith(root) || destination == root) throw UnsafeArchiveException("entry escapes the target directory")
        return destination
    }
}

// The common bug, for comparison: trusts entry names.
fun naiveUnzip(zip: InputStream, target: Path) {
    ZipInputStream(zip).use { entries ->
        while (true) {
            val entry = entries.nextEntry ?: break
            val destination = target.resolve(entry.name)
            destination.parent.createDirectories()
            Files.copy(entries, destination)
        }
    }
}

fun zipOf(vararg files: Pair<String, ByteArray>): ByteArray {
    val bytes = ByteArrayOutputStream()
    ZipOutputStream(bytes).use { zip ->
        for ((name, content) in files) {
            zip.putNextEntry(ZipEntry(name))
            zip.write(content)
            zip.closeEntry()
        }
    }
    return bytes.toByteArray()
}

fun main() {
    val sandbox = createTempDirectory("zipslip")
    val target = sandbox.resolve("uploads").createDirectories()
    val evil = zipOf("report.txt" to "ok".toByteArray(), "../escaped.txt" to "pwned".toByteArray())

    naiveUnzip(ByteArrayInputStream(evil), target)
    println("naive: escaped.txt outside target exists = ${sandbox.resolve("escaped.txt").exists()}")

    val safeTarget = sandbox.resolve("safe").createDirectories()
    val result = runCatching { SafeUnzip.extract(ByteArrayInputStream(evil), safeTarget) }
    println("safe: ${result.exceptionOrNull()?.message}")
    val fine = SafeUnzip.extract(ByteArrayInputStream(zipOf("docs/a.txt" to "hello".toByteArray())), sandbox.resolve("fine"))
    println("extracted ${fine.map { sandbox.relativize(it) }}: ${fine.single().readText()}")
}
