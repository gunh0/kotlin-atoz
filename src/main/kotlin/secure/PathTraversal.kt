package atoz.secure

import java.nio.file.Path
import kotlin.io.path.exists

/*
Path traversal: a user-supplied name like "../../etc/passwd" or an absolute
"/etc/passwd" escapes the directory you meant to serve from.
1. Resolve against an absolute, normalized base, then `normalize()` to collapse
   `.` and `..` segments.
2. Check `resolved.startsWith(base)` on Path, not on String: as strings,
   "/srv/files-evil" starts with "/srv/files"; as paths it does not, because
   Path compares whole name elements.
3. `normalize()` is purely lexical. A symlink inside the base can still point
   outside, so for existing files also compare `toRealPath()` of both sides.
Same API and same pitfalls as in Java; Kotlin's `Path.div` (`base / name`) is
just `resolve`, not a safety check.
*/

class SafeDirectory(base: Path) {
    private val root: Path = base.toAbsolutePath().normalize()

    fun resolve(userPath: String): Path {
        val candidate = root.resolve(userPath).normalize()
        require(candidate.startsWith(root)) { "path escapes the base directory" }
        if (candidate.exists()) {
            require(candidate.toRealPath().startsWith(root.toRealPath())) { "path escapes the base directory via a link" }
        }
        return candidate
    }
}

fun naiveStringCheck(base: String, userPath: String): Boolean =
    Path.of(base, userPath).normalize().toString().startsWith(base)

fun main() {
    val dir = SafeDirectory(Path.of("/srv/files"))
    for (name in listOf("report.pdf", "img/../report.pdf", "../../etc/passwd", "/etc/passwd", "../files-evil/x")) {
        println("$name -> ${runCatching { dir.resolve(name) }.fold({ it.toString() }, { "rejected: ${it.message}" })}")
    }
    println("string check accepts ../files-evil/x: ${naiveStringCheck("/srv/files", "../files-evil/x")}")
}
