package atoz.collections

import kotlin.test.Test
import kotlin.test.assertEquals

class SortingTest {
    private val people = listOf(
        Contributor("dana", 12, "infra"),
        Contributor("ari", 40, null),
        Contributor("bo", 12, "app"),
        Contributor("cy", 40, "app"),
    )

    @Test
    fun multipleKeys() {
        assertEquals(listOf("ari", "cy", "bo", "dana"), ranking(people))
    }

    @Test
    fun nullsLastThenTieBreaker() {
        assertEquals(listOf("bo", "cy", "dana", "ari"), byTeamThenName(people))
    }

    @Test
    fun sortingIsStable() {
        assertEquals(listOf("dana", "bo", "ari", "cy"), stableByCommits(people))
    }

    @Test
    fun inPlaceVersusCopy() {
        val original = listOf(3, 1, 2)
        assertEquals(listOf(1, 2, 3), original.sorted())
        assertEquals(listOf(3, 1, 2), original)
        val names = mutableListOf("bob", "Alice", "carol")
        sortInPlace(names)
        assertEquals(listOf("Alice", "bob", "carol"), names)
    }
}
