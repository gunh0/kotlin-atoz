package atoz.collections

// Turning a list into a map, without Collectors:
// - groupBy { key } -> Map<K, List<V>>, every element kept under its key
// - groupingBy { key }.eachCount() -> Map<K, Int>, counting without lists
// - associateBy { key } -> Map<K, V>; associateWith { value } -> Map<V, R>;
//   associate { k to v } -> Map<K, V>
// - partition { pred } -> Pair(matching, rest)
// associate* keep the LAST element for a duplicate key, silently. Java's
// Collectors.toMap throws on duplicates instead; use groupBy when keys repeat.
// All of them keep the first-seen order of keys.

data class LoginEvent(val user: String, val ip: String, val success: Boolean)

fun failuresByUser(events: List<LoginEvent>): Map<String, Int> =
    events.filterNot { it.success }.groupingBy { it.user }.eachCount()

fun ipsByUser(events: List<LoginEvent>): Map<String, List<String>> =
    events.groupBy(keySelector = { it.user }, valueTransform = { it.ip })

fun lastEventByUser(events: List<LoginEvent>): Map<String, LoginEvent> = events.associateBy { it.user }

fun userNameLengths(users: List<String>): Map<String, Int> = users.associateWith { it.length }

fun splitByOutcome(events: List<LoginEvent>): Pair<List<LoginEvent>, List<LoginEvent>> = events.partition { it.success }

fun main() {
    val events = listOf(
        LoginEvent("alice", "10.0.0.1", true),
        LoginEvent("bob", "10.0.0.2", false),
        LoginEvent("bob", "10.0.0.3", false),
        LoginEvent("bob", "10.0.0.2", true),
        LoginEvent("carol", "10.0.0.4", false),
    )
    println("failures: ${failuresByUser(events)}")
    println("ips: ${ipsByUser(events)}")
    println("last event per user (duplicates overwritten): ${lastEventByUser(events).mapValues { it.value.success }}")
    println("name lengths: ${userNameLengths(listOf("alice", "bob"))}")
    val (ok, failed) = splitByOutcome(events)
    println("${ok.size} ok, ${failed.size} failed")
}
