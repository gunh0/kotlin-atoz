package atoz.oop

// A sealed class or interface fixes its set of direct subtypes at compile time:
// they must live in the same package and module. In exchange, `when` over a
// sealed type is exhaustive without an `else`, and adding a new subtype turns
// every non-exhaustive `when` into a compile error instead of a runtime bug.
// Java 17's sealed interfaces + records are the counterpart.
//
// Unlike enum constants, each subtype can carry its own data. A stateless case
// is a `data object`: a singleton with a readable toString() ("Loading") and
// an equals() that survives serialization.

sealed interface FetchResult {
    data class Success(val body: String) : FetchResult
    data class Failure(val code: Int, val reason: String) : FetchResult
    data object Loading : FetchResult
}

fun renderFetch(result: FetchResult): String = when (result) {
    is FetchResult.Success -> "200 OK (${result.body.length} bytes)"
    is FetchResult.Failure -> "error ${result.code}: ${result.reason}"
    FetchResult.Loading -> "loading..."
}

fun isRetryable(result: FetchResult): Boolean = when (result) {
    is FetchResult.Failure -> result.code >= 500
    is FetchResult.Success, FetchResult.Loading -> false
}

fun main() {
    val results = listOf(
        FetchResult.Loading,
        FetchResult.Success("{\"ok\":true}"),
        FetchResult.Failure(503, "unavailable"),
        FetchResult.Failure(404, "not found"),
    )
    results.forEach { println("$it -> ${renderFetch(it)}, retryable=${isRetryable(it)}") }
}
