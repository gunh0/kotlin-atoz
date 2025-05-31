package atoz.collections

// Operations that look at neighbours or groups of elements:
// - chunked(n): consecutive, non-overlapping blocks (the last may be shorter)
// - windowed(size, step, partialWindows): sliding windows, overlapping when
//   step < size; partial windows at the end are dropped unless asked for
// - zip(other): pairs elements by position, stopping at the shorter list
// - zipWithNext(): each element with its successor, ideal for deltas
// - unzip(): a list of pairs back into a pair of lists
// Each takes an optional transform lambda that avoids building the
// intermediate lists.

fun batches(ids: List<Int>, size: Int): List<List<Int>> = ids.chunked(size)

fun movingAverage(values: List<Double>, window: Int): List<Double> = values.windowed(window) { it.average() }

// Flags each window of `window` consecutive attempts that are all failures.
fun bruteForceWindows(attempts: List<Boolean>, window: Int): List<Int> =
    attempts.windowed(window).withIndex().filter { (_, w) -> w.none { it } }.map { it.index }

fun deltas(readings: List<Int>): List<Int> = readings.zipWithNext { a, b -> b - a }

fun labelled(names: List<String>, scores: List<Int>): List<String> = names.zip(scores) { n, s -> "$n=$s" }

fun splitPairs(pairs: List<Pair<String, Int>>): Pair<List<String>, List<Int>> = pairs.unzip()

fun main() {
    println("batches: ${batches((1..7).toList(), 3)}")
    println("moving average: ${movingAverage(listOf(1.0, 2.0, 3.0, 4.0, 5.0), 3)}")
    println("windowed(3, step 2, partial): ${(1..6).toList().windowed(3, step = 2, partialWindows = true)}")
    println("brute force windows: ${bruteForceWindows(listOf(true, false, false, false, true, false), 3)}")
    println("deltas: ${deltas(listOf(10, 13, 13, 9))}")
    println("labelled: ${labelled(listOf("a", "b", "c"), listOf(1, 2))}")
    println("unzip: ${splitPairs(listOf("x" to 1, "y" to 2))}")
}
