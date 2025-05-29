package atoz.coroutines

import java.nio.file.Path
import java.util.concurrent.Executors
import kotlin.io.path.createTempFile
import kotlin.io.path.readText
import kotlin.io.path.writeText
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/*
Dispatchers decide which threads a coroutine runs on:
- Dispatchers.Default: a pool sized to the CPU cores, for CPU-bound work.
- Dispatchers.IO: shares those threads but allows many more of them to block,
  for blocking calls such as file or JDBC access.
- Any Java Executor becomes a dispatcher via `asCoroutineDispatcher()`
  (close it afterwards, as you would shut down the executor).
`withContext(dispatcher) { }` switches threads for one block and returns its
result, then resumes on the caller's dispatcher. The caller's code stays
sequential; no callbacks, no Future juggling.
*/

suspend fun sumOfSquares(n: Int): Long = withContext(Dispatchers.Default) {
    (1..n).sumOf { it.toLong() * it }
}

suspend fun readConfig(path: Path): String = withContext(Dispatchers.IO) { path.readText() }

suspend fun threadNameOn(dispatcher: CoroutineDispatcher): String =
    withContext(dispatcher) { Thread.currentThread().name }

fun namedDispatcher(name: String): ExecutorCoroutineDispatcher =
    Executors.newSingleThreadExecutor { runnable -> Thread(runnable, name) }.asCoroutineDispatcher()

fun main() = runBlocking {
    println("caller runs on ${Thread.currentThread().name}")
    println("sumOfSquares(1000) = ${sumOfSquares(1_000)}")
    println("Default runs on ${threadNameOn(Dispatchers.Default)}")
    namedDispatcher("audit-writer").use { println("custom runs on ${threadNameOn(it)}") }
    val file = createTempFile(suffix = ".conf").apply { writeText("port=8443") }
    println("read on IO: ${readConfig(file)}")
}
