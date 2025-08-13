package atoz.functional

// Result<T> holds either a value or the Throwable that prevented it.
// runCatching { } produces one; map/mapCatching transform the value,
// recover/recoverCatching turn a failure into a value, and the result is
// unpacked with getOrNull, getOrElse, getOrThrow or fold(onSuccess, onFailure).
// onSuccess/onFailure add side effects without unpacking.
//
// map runs its lambda outside the catch (an exception there propagates);
// mapCatching catches it into the Result. Caveat: runCatching catches every
// Throwable, including Errors and coroutine CancellationException, so keep it
// at boundaries (parsing, I/O) rather than around arbitrary code.

data class PortSetting(val port: Int)

fun parsePort(raw: String): Result<Int> = runCatching { raw.trim().toInt() }

fun loadPortSetting(raw: String): Result<PortSetting> =
    parsePort(raw).mapCatching { port ->
        require(port in 1..65535) { "port out of range: $port" }
        PortSetting(port)
    }

fun portOrDefault(raw: String): Int = loadPortSetting(raw).map { it.port }.getOrElse { 8080 }

fun describePortSetting(raw: String): String = loadPortSetting(raw).fold(
    onSuccess = { "listening on ${it.port}" },
    onFailure = { "invalid (${it::class.simpleName}: ${it.message})" },
)

fun firstWorkingPort(candidates: List<String>, log: MutableList<String>): Result<PortSetting> =
    candidates.fold(Result.failure<PortSetting>(NoSuchElementException("no candidates"))) { acc, raw ->
        acc.recoverCatching { loadPortSetting(raw).onFailure { log += "skip '$raw'" }.getOrThrow() }
    }

fun main() {
    listOf("443", " 8443 ", "http", "70000").forEach { println("'$it' -> ${describePortSetting(it)}") }
    println("portOrDefault(\"x\") = ${portOrDefault("x")}")
    val log = mutableListOf<String>()
    println("first working: ${firstWorkingPort(listOf("x", "0", "9000", "9001"), log).getOrNull()}, $log")
}
