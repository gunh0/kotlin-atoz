package atoz.collections

/*
Checking order (stable in 2.4): `isSorted()`, `isSortedDescending()`,
`isSortedBy { }`, `isSortedByDescending { }` and `isSortedWith(comparator)`
on iterables, arrays and sequences. Before, you wrote
`zipWithNext().all { (a, b) -> a <= b }` or compared with a sorted copy
(Java has no built-in either).
- "Sorted" means non-decreasing: equal neighbours pass. For strictly
  increasing values (sequence numbers, nonces) still check `a < b` yourself.
- The check stops at the first out-of-order pair, also on lazy sequences.
- `binarySearch` assumes sorted input and returns a meaningless index
  otherwise, without failing: check `isSorted` first where input is untrusted.
*/

data class AuditRecord(val sequence: Long, val epochSecond: Long, val action: String)

fun isChronological(records: List<AuditRecord>): Boolean = records.isSortedBy { it.epochSecond }

// Gaps are fine, repeats and reordering are not: a replayed record reuses a sequence number.
fun hasStrictlyIncreasingSequence(records: List<AuditRecord>): Boolean =
    records.zipWithNext().all { (a, b) -> a.sequence < b.sequence }

fun indexOfBlockedPort(sortedBlockedPorts: List<Int>, port: Int): Int {
    require(sortedBlockedPorts.isSorted()) { "binarySearch needs sorted input" }
    return sortedBlockedPorts.binarySearch(port)
}

fun main() {
    val log = listOf(AuditRecord(1, 100, "login"), AuditRecord(2, 100, "read"), AuditRecord(2, 105, "delete"))
    println("chronological: ${isChronological(log)}, strictly increasing seq: ${hasStrictlyIncreasingSequence(log)}")
    println("[1, 1, 2].isSorted() = ${listOf(1, 1, 2).isSorted()}, descending: ${intArrayOf(9, 5, 5).isSortedDescending()}")
    println("binarySearch on unsorted: ${listOf(443, 22, 80).binarySearch(443)}")
    println("guarded: ${runCatching { indexOfBlockedPort(listOf(443, 22, 80), 443) }.exceptionOrNull()?.message}")
}
