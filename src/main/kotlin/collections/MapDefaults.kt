package atoz.collections

/*
Map lookups with defaults, and the operations that quietly lose data.
- `withDefault { }` returns a wrapper whose default only applies to
  `getValue(key)`; `map[key]` still returns null, and the default is never
  stored. Plain `getValue` on a missing key throws NoSuchElementException.
- `getOrElse(key) { }` treats a key mapped to null like a missing key; Java's
  `getOrDefault` returns that null.
- `mapKeys` keeps the last value when two keys collide, silently. Normalizing
  HTTP header names ("Host" / "host") can drop one: group first to detect it.
- `keys`, `values` and `entries` of a MutableMap are live views (removing from
  them removes entries, as in Java); `filterKeys`/`filterValues` return copies.
- `toSortedMap()` gives a stable, sorted order for output and comparisons.
*/

val requestsPerMinute: Map<String, Int> = mapOf("admin" to 600, "service" to 6_000).withDefault { 60 }

fun limitFor(role: String): Int = requestsPerMinute.getValue(role)

fun lowercaseHeaders(headers: List<Pair<String, String>>): Map<String, String> =
    headers.toMap().mapKeys { (name, _) -> name.lowercase() }

fun duplicateHeaderNames(headers: List<Pair<String, String>>): Set<String> =
    headers.groupBy({ it.first.lowercase() }, { it.second }).filterValues { it.size > 1 }.keys

fun main() {
    println("admin=${limitFor("admin")} guest=${limitFor("guest")} get(guest)=${requestsPerMinute["guest"]}")
    val headers = listOf("Host" to "api.example.com", "host" to "evil.example", "Accept" to "*/*")
    println("lowercased: ${lowercaseHeaders(headers)}")
    println("duplicates: ${duplicateHeaderNames(headers)}")
    val sessions = mutableMapOf("s1" to "alice", "s2" to "bob", "s3" to "alice")
    sessions.values.removeAll { it == "alice" } // live view: removes the entries
    println("after logout of alice: $sessions")
    println("sorted: ${mapOf("b" to 2, "a" to 1).toSortedMap()}")
}
