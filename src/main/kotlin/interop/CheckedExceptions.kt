package atoz.interop

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

/*
Kotlin has no checked exceptions. Java methods that declare `throws
IOException` can be called without try/catch, and Kotlin functions never have
to declare what they throw.
The catch is on the Java side: javac rejects `catch (IOException e)` around a
call that doesn't declare it ("exception is never thrown in body of
corresponding try statement"). Annotate Kotlin functions called from Java
with @Throws(IOException::class) to add the `throws` clause to the bytecode.
`use { }` is Kotlin's try-with-resources; runCatching turns any exception
into a Result value when you'd rather not throw.
*/

@Throws(IOException::class)
fun readFirstLine(path: Path): String =
    Files.newBufferedReader(path).use { reader ->
        reader.readLine() ?: throw IOException("$path is empty")
    }

// Same body, but Java callers can't catch IOException from it without a compile error.
fun readFirstLineUndeclared(path: Path): String =
    Files.newBufferedReader(path).use { it.readLine() ?: throw IOException("$path is empty") }

fun firstLineOrNull(path: Path): String? = runCatching { readFirstLine(path) }.getOrNull()

fun main() {
    val file = Files.createTempFile("atoz", ".txt")
    Files.writeString(file, "first\nsecond")
    println(readFirstLine(file))
    println(firstLineOrNull(file.resolveSibling("does-not-exist.txt")))
    val facade = Class.forName("atoz.interop.CheckedExceptionsKt")
    for (name in listOf("readFirstLine", "readFirstLineUndeclared")) {
        println("$name throws ${facade.getMethod(name, Path::class.java).exceptionTypes.map { it.simpleName }}")
    }
}
