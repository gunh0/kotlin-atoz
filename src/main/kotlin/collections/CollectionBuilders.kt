package atoz.collections

// Factory functions instead of `new ArrayList<>()` + add():
// listOf/setOf/mapOf build read-only collections, mutableListOf/... mutable
// ones, emptyList() an empty one, listOfNotNull() skips nulls, and List(n) { }
// fills by index. setOf and mapOf keep insertion order (LinkedHashSet /
// LinkedHashMap), unlike Java's Set.of/Map.of, whose order is unspecified.
//
// buildList/buildSet/buildMap give a mutable collection inside the lambda and
// return it read-only, so conditional or looped construction doesn't leak a
// mutable reference.

fun defaultHeaders(token: String?, json: Boolean): Map<String, String> = buildMap {
    put("User-Agent", "atoz/1.0")
    if (token != null) put("Authorization", "Bearer $token")
    if (json) {
        put("Accept", "application/json")
        put("Content-Type", "application/json")
    }
}

fun commandLine(binary: String, verbose: Boolean, files: List<String>): List<String> = buildList {
    add(binary)
    if (verbose) add("-v")
    addAll(files)
}

fun squares(n: Int): List<Int> = List(n) { it * it }

fun optionalFlags(debug: String?, trace: String?): List<String> = listOfNotNull(debug, trace)

fun main() {
    println(listOf(3, 1, 2))
    println(setOf("b", "a", "b", "c")) // duplicates dropped, order kept
    println(mapOf("z" to 1, "a" to 2)) // insertion order
    println(defaultHeaders("t0k3n", json = true))
    println(commandLine("grep", verbose = true, files = listOf("a.log", "b.log")))
    println(squares(5))
    println(optionalFlags("--debug", null))
    val mutable = mutableListOf(1, 2)
    mutable += 3
    println(mutable)
}
