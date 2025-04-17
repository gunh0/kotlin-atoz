package atoz.collections

// The collection operations Java reaches through stream()...collect() are
// plain extension functions on Iterable in Kotlin, and each one returns a new
// list right away (eager; sequences are the lazy alternative).
// - map / mapIndexed / mapNotNull: transform (and drop nulls)
// - filter / filterNot / filterIsInstance<T>(): select
// - flatMap / flatten: one-to-many, nested lists into one
// - distinct / distinctBy, take / drop / takeWhile / dropWhile
// They chain naturally, and every intermediate value is an ordinary list.

data class AccessLog(val ip: String, val path: String, val status: Int)

fun parseAccessLog(lines: List<String>): List<AccessLog> = lines.mapNotNull { line ->
    val parts = line.trim().split(Regex("\\s+"))
    if (parts.size != 3) return@mapNotNull null
    val status = parts[2].toIntOrNull() ?: return@mapNotNull null
    AccessLog(parts[0], parts[1], status)
}

fun suspiciousIps(logs: List<AccessLog>): List<String> =
    logs.filter { it.status == 401 || it.status == 403 }.map { it.ip }.distinct()

fun pathSegments(logs: List<AccessLog>): List<String> =
    logs.flatMap { it.path.split('/').filter(String::isNotEmpty) }.distinct()

fun numbersOnly(values: List<Any>): List<Int> = values.filterIsInstance<Int>()

fun headerUntilBlank(lines: List<String>): List<String> = lines.takeWhile { it.isNotBlank() }

fun main() {
    val raw = listOf(
        "10.0.0.5 /admin/login 401",
        "10.0.0.5 /admin/login 401",
        "garbage line",
        "10.0.0.9 /api/users 200",
        "10.0.0.7 /admin/config 403",
    )
    val logs = parseAccessLog(raw)
    println("parsed ${logs.size} of ${raw.size} lines")
    println("suspicious: ${suspiciousIps(logs)}")
    println("segments: ${pathSegments(logs)}")
    println("numbersOnly: ${numbersOnly(listOf(1, "two", 3.0, 4))}")
    println("header: ${headerUntilBlank(listOf("Host: a", "Accept: */*", "", "body"))}")
    println(listOf(listOf(1, 2), listOf(3)).flatten().mapIndexed { i, v -> "$i:$v" })
}
