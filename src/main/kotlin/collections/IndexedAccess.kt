package atoz.collections

// Index-based work without manual counters:
// - indices (0..lastIndex) and lastIndex for loops over positions
// - withIndex() yields (index, value) pairs; forEachIndexed/mapIndexed/
//   filterIndexed pass the index to the lambda
// - getOrNull(i) / getOrElse(i) { } instead of a bounds check followed by get;
//   list[i] out of range still throws IndexOutOfBoundsException
// - indexOf / indexOfFirst / indexOfLast return -1 when nothing matches, as
//   in Java, so prefer firstOrNull or an explicit check over trusting the result

fun numbered(lines: List<String>): List<String> = lines.withIndex().map { (i, line) -> "${i + 1}: $line" }

fun evenPositions(items: List<String>): List<String> = items.filterIndexed { i, _ -> i % 2 == 0 }

fun argumentOr(args: List<String>, position: Int, default: String): String = args.getOrElse(position) { default }

fun firstErrorLine(lines: List<String>): Int = lines.indexOfFirst { it.startsWith("ERROR") }

fun mirrorPairs(items: List<Int>): List<Pair<Int, Int>> =
    (0 until items.size / 2).map { i -> items[i] to items[items.lastIndex - i] }

fun main() {
    val lines = listOf("INFO start", "WARN slow", "ERROR failed", "INFO stop")
    numbered(lines).forEach(::println)
    println("even positions: ${evenPositions(lines)}")
    println("arg 0: ${argumentOr(listOf("--port"), 0, "?")}, arg 3: ${argumentOr(listOf("--port"), 3, "?")}")
    println("first error at ${firstErrorLine(lines)}, in a clean log: ${firstErrorLine(listOf("INFO ok"))}")
    println("indices: ${lines.indices}, lastIndex: ${lines.lastIndex}")
    println("mirror pairs: ${mirrorPairs(listOf(1, 2, 3, 4, 5))}")
}
