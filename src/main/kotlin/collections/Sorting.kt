package atoz.collections

// sorted()/sortedDescending() use natural order; sortedBy { key } and
// sortedByDescending sort by one property; sortedWith(comparator) handles the
// rest. compareBy(a, b) builds a comparator from several keys, and
// thenBy/thenByDescending append tie-breakers, replacing Java's
// Comparator.comparing(...).thenComparing(...). nullsLast()/nullsFirst() place
// nulls explicitly.
//
// The sorted* functions return a new list; sort()/sortBy() sort a MutableList
// in place. All of them are stable: equal elements keep their relative order.

data class Contributor(val name: String, val commits: Int, val team: String?)

val contributorOrder: Comparator<Contributor> =
    compareByDescending<Contributor> { it.commits }.thenBy { it.name }

fun ranking(people: List<Contributor>): List<String> = people.sortedWith(contributorOrder).map { it.name }

fun byTeamThenName(people: List<Contributor>): List<String> =
    people.sortedWith(compareBy<Contributor, String?>(nullsLast()) { it.team }.thenBy { it.name }).map { it.name }

fun stableByCommits(people: List<Contributor>): List<String> = people.sortedBy { it.commits }.map { it.name }

fun sortInPlace(names: MutableList<String>) {
    names.sortWith(String.CASE_INSENSITIVE_ORDER)
}

fun main() {
    val people = listOf(
        Contributor("dana", 12, "infra"),
        Contributor("ari", 40, null),
        Contributor("bo", 12, "app"),
        Contributor("cy", 40, "app"),
    )
    println("ranking: ${ranking(people)}")
    println("by team then name: ${byTeamThenName(people)}")
    println("stable by commits: ${stableByCommits(people)}")
    println("sorted: ${listOf(3, 1, 2).sorted()}, descending: ${listOf(3, 1, 2).sortedDescending()}")
    val names = mutableListOf("bob", "Alice", "carol")
    sortInPlace(names)
    println("in place: $names")
}
