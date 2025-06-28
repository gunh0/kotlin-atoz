package atoz.collections

// Sets support the mathematical operations as infix functions: union,
// intersect, subtract (plus `+` and `-`). `in` calls contains().
//
// Map lookups: map[key] returns null for a missing key (no exception, and
// no way to tell "absent" from "present with value null" without containsKey);
// getValue(key) throws NoSuchElementException; getOrElse/getOrDefault supply a
// fallback. `map + (k to v)` and `map - k` return new maps. mapValues,
// filterKeys, filterValues transform without loops, and getOrPut on a
// MutableMap is the computeIfAbsent idiom.

val rolePermissions: Map<String, Set<String>> = mapOf(
    "viewer" to setOf("read"),
    "editor" to setOf("read", "write"),
    "admin" to setOf("read", "write", "delete", "manage-users"),
)

fun permissionsOf(roles: Set<String>): Set<String> =
    roles.fold(emptySet<String>()) { acc, role -> acc union rolePermissions.getOrElse(role) { emptySet() } }

fun missingPermissions(roles: Set<String>, required: Set<String>): Set<String> = required subtract permissionsOf(roles)

fun sharedPermissions(a: String, b: String): Set<String> = rolePermissions.getValue(a) intersect rolePermissions.getValue(b)

fun permissionCounts(): Map<String, Int> = rolePermissions.mapValues { (_, perms) -> perms.size }

fun indexByFirstLetter(words: List<String>): Map<Char, MutableList<String>> {
    val index = mutableMapOf<Char, MutableList<String>>()
    for (w in words) index.getOrPut(w.first()) { mutableListOf() } += w
    return index
}

fun main() {
    println("editor+viewer: ${permissionsOf(setOf("editor", "viewer"))}")
    println("missing for viewer: ${missingPermissions(setOf("viewer"), setOf("read", "delete"))}")
    println("shared editor/admin: ${sharedPermissions("editor", "admin")}")
    println("counts: ${permissionCounts()}")
    println("map[\"root\"] = ${rolePermissions["root"]}, getValue -> ${runCatching { rolePermissions.getValue("root") }.exceptionOrNull()}")
    val withAuditor = rolePermissions + ("auditor" to setOf("read", "audit"))
    println("keys after +: ${withAuditor.keys}, original keys: ${rolePermissions.keys}")
    println("index: ${indexByFirstLetter(listOf("apple", "avocado", "banana"))}")
}
