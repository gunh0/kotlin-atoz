package atoz.functional

// fold(initial) { acc, x -> ... } walks a collection carrying an accumulator;
// the accumulator may have a different type than the elements, which makes
// fold the general tool behind sum, joinToString, groupBy and friends.
// reduce is fold that starts from the first element instead, so its result has
// the element type and it throws UnsupportedOperationException on an empty
// collection; reduceOrNull returns null instead.
//
// runningFold (alias scan) keeps every intermediate accumulator, handy for
// running balances. foldRight walks from the end, which matters when the
// operation isn't commutative.

data class Txn(val id: String, val amount: Long)

fun balance(txns: List<Txn>, opening: Long = 0): Long = txns.fold(opening) { acc, t -> acc + t.amount }

fun runningBalances(txns: List<Txn>, opening: Long = 0): List<Long> = txns.runningFold(opening) { acc, t -> acc + t.amount }

fun firstOverdraft(txns: List<Txn>): String? =
    txns.zip(runningBalances(txns).drop(1)).firstOrNull { (_, after) -> after < 0 }?.first?.id

fun longestWord(words: List<String>): String = words.reduce { best, w -> if (w.length > best.length) w else best }

fun longestWordOrNull(words: List<String>): String? = words.reduceOrNull { best, w -> if (w.length > best.length) w else best }

fun digitsToNumber(digits: List<Int>): Int = digits.fold(0) { acc, d -> acc * 10 + d }

fun foldLeftText(parts: List<String>): String = parts.fold("") { acc, p -> "($acc$p)" }
fun foldRightText(parts: List<String>): String = parts.foldRight("") { p, acc -> "($p$acc)" }

fun main() {
    val txns = listOf(Txn("t1", 100), Txn("t2", -30), Txn("t3", -90), Txn("t4", 50))
    println("balance = ${balance(txns)}, running = ${runningBalances(txns)}")
    println("first overdraft: ${firstOverdraft(txns)}")
    println("longest: ${longestWord(listOf("fold", "reduce", "scan"))}, of empty: ${longestWordOrNull(emptyList())}")
    println("digits -> ${digitsToNumber(listOf(4, 0, 9, 6))}")
    println("fold: ${foldLeftText(listOf("a", "b", "c"))}, foldRight: ${foldRightText(listOf("a", "b", "c"))}")
}
