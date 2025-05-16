package atoz.basics

// `for` iterates over anything with an iterator(): ranges, collections,
// strings, `indices`. There is no C-style `for (int i = 0; ...)`.
// `while`, `do-while` and `repeat(n)` cover the rest.
//
// A label (`outer@`) lets `break@outer` / `continue@outer` leave an enclosing
// loop, like Java's labelled break. Inside a lambda such as forEach, plain
// `return` would leave the whole function (forEach is inline); `return@forEach`
// only ends the current element, i.e. it behaves like `continue`.

fun firstPairSummingTo(numbers: List<Int>, target: Int): Pair<Int, Int>? {
    var found: Pair<Int, Int>? = null
    outer@ for (i in numbers.indices) {
        for (j in i + 1..numbers.lastIndex) {
            if (numbers[i] + numbers[j] == target) {
                found = numbers[i] to numbers[j]
                break@outer
            }
        }
    }
    return found
}

// Sums of the rows that contain no zero.
fun sumsOfRowsWithoutZero(grid: List<List<Int>>): List<Int> {
    val sums = mutableListOf<Int>()
    rows@ for (row in grid) {
        var sum = 0
        for (cell in row) {
            if (cell == 0) continue@rows
            sum += cell
        }
        sums += sum
    }
    return sums
}

fun collatzSteps(start: Int): Int {
    var n = start
    var steps = 0
    while (n != 1) {
        n = if (n % 2 == 0) n / 2 else 3 * n + 1
        steps++
    }
    return steps
}

fun digitCount(value: Int): Int {
    var rest = value
    var digits = 0
    do {
        digits++
        rest /= 10
    } while (rest != 0)
    return digits
}

fun evensKeptByForEach(numbers: List<Int>): List<Int> {
    val kept = mutableListOf<Int>()
    numbers.forEach {
        if (it % 2 != 0) return@forEach
        kept += it
    }
    return kept
}

fun main() {
    println("pair summing to 10: ${firstPairSummingTo(listOf(1, 4, 6, 9), 10)}")
    println("row sums without zero: ${sumsOfRowsWithoutZero(listOf(listOf(1, 2), listOf(3, 0), listOf(4, 5)))}")
    println("collatz(27) takes ${collatzSteps(27)} steps")
    println("digits in 0: ${digitCount(0)}, in 12345: ${digitCount(12345)}")
    println("evens: ${evensKeptByForEach((1..10).toList())}")
    for ((index, ch) in "abc".withIndex()) print("$index=$ch ")
    println()
    repeat(3) { print("tick ") }
    println()
}
