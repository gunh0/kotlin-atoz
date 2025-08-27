package atoz.collections

// Reducing a collection to one value, without a stream and a collector:
// count { }, sumOf { } (overloads for Int, Long, Double, UInt, BigDecimal,
// chosen by the selector's type), average(), min()/max(), minOf { }/maxOf { },
// maxByOrNull { } for the element itself, and any/all/none for predicates.
//
// Empty collections are where they differ: sum is 0 and average() is NaN;
// min()/max()/maxBy() throw NoSuchElementException (since Kotlin 1.7) while
// the ...OrNull variants return null; all { } on an empty collection is true
// (vacuous truth), which matters in checks like "all certificates are valid".

data class Request(val path: String, val millis: Long, val bytes: Int)

fun slowRequests(requests: List<Request>, thresholdMillis: Long): Int = requests.count { it.millis > thresholdMillis }

fun totalBytes(requests: List<Request>): Long = requests.sumOf { it.bytes.toLong() }

fun averageMillis(requests: List<Request>): Double = requests.map { it.millis }.average()

fun slowest(requests: List<Request>): Request? = requests.maxByOrNull { it.millis }

fun longestPathLength(requests: List<Request>): Int = requests.maxOf { it.path.length }

fun allFast(requests: List<Request>, thresholdMillis: Long): Boolean = requests.all { it.millis <= thresholdMillis }

fun main() {
    val requests = listOf(
        Request("/", 12, 512),
        Request("/search", 340, 20_480),
        Request("/api/v1/users", 95, Int.MAX_VALUE),
    )
    println("slow: ${slowRequests(requests, 100)}, total bytes: ${totalBytes(requests)}")
    println("average: ${averageMillis(requests)} ms, slowest: ${slowest(requests)?.path}")
    println("longest path: ${longestPathLength(requests)}, all under 500 ms: ${allFast(requests, 500)}")
    println("empty: average=${averageMillis(emptyList())}, slowest=${slowest(emptyList())}, allFast=${allFast(emptyList(), 0)}")
    println("emptyList<Int>().max() -> ${runCatching { emptyList<Int>().max() }.exceptionOrNull()}")
}
