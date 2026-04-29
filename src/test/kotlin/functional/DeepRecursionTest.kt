package atoz.functional

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DeepRecursionTest {
    private val tree = Nested.Group(
        listOf(Nested.Leaf(1), Nested.Group(listOf(Nested.Leaf(2), Nested.Group(listOf(Nested.Leaf(3)))))),
    )

    @Test
    fun bothVersionsAgreeOnShallowInput() {
        assertEquals(3, naiveDepth(tree))
        assertEquals(3, deepDepth(tree))
        assertEquals(6L, deepSum(tree))
        assertEquals(0, deepDepth(Nested.Leaf(7)))
    }

    @Test
    fun naiveRecursionOverflowsTheStack() {
        assertFailsWith<StackOverflowError> { naiveDepth(nestedChain(depth = 200_000)) }
    }

    @Test
    fun deepRecursiveFunctionHandlesTheSameInput() {
        val hostile = nestedChain(depth = 200_000, leaf = 5)
        assertEquals(200_000, deepDepth(hostile))
        assertEquals(5L, deepSum(hostile))
    }

    @Test
    fun depthLimitRejectsDeepInput() {
        assertEquals(tree, requireMaxDepth(tree, maxDepth = 3))
        assertFailsWith<IllegalArgumentException> { requireMaxDepth(nestedChain(depth = 65), maxDepth = 64) }
    }
}
