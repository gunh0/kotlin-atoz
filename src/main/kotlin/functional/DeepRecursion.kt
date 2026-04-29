package atoz.functional

/*
Recursion is the natural way to walk nested data (JSON, XML, ASTs, menus),
but each call takes a JVM stack frame. Input nested 100,000 levels deep, which
an attacker can send in a few hundred kilobytes, ends in StackOverflowError,
and `tailrec` can't help because visiting a tree is not tail recursive.
`DeepRecursiveFunction` (stdlib, stable since 1.7) keeps the recursion on the
heap: inside its block, `callRecursive(x)` is a suspending call, so the code
reads like plain recursion while its depth is limited only by memory.
In Java you would rewrite the walk with an explicit Deque by hand.
It still doesn't make deep input safe to accept: bound the nesting depth
where the input is parsed, as Jackson's StreamReadConstraints do.
*/

sealed interface Nested {
    data class Leaf(val value: Int) : Nested

    class Group(val children: List<Nested>) : Nested
}

// Built iteratively, so creating the deep structure itself needs no stack.
fun nestedChain(depth: Int, leaf: Int = 1): Nested {
    var node: Nested = Nested.Leaf(leaf)
    repeat(depth) { node = Nested.Group(listOf(node)) }
    return node
}

fun naiveDepth(node: Nested): Int = when (node) {
    is Nested.Leaf -> 0
    is Nested.Group -> 1 + (node.children.maxOfOrNull(::naiveDepth) ?: 0)
}

val deepDepth = DeepRecursiveFunction<Nested, Int> { node ->
    when (node) {
        is Nested.Leaf -> 0
        is Nested.Group -> 1 + (node.children.maxOfOrNull { callRecursive(it) } ?: 0)
    }
}

val deepSum = DeepRecursiveFunction<Nested, Long> { node ->
    when (node) {
        is Nested.Leaf -> node.value.toLong()
        is Nested.Group -> node.children.sumOf { callRecursive(it) }
    }
}

// Depth checks at the boundary: reject before doing any real work.
fun requireMaxDepth(node: Nested, maxDepth: Int): Nested {
    require(deepDepth(node) <= maxDepth) { "input nested deeper than $maxDepth levels" }
    return node
}

fun main() {
    val shallow = Nested.Group(listOf(Nested.Leaf(1), Nested.Group(listOf(Nested.Leaf(2), Nested.Leaf(3)))))
    println("shallow: naive depth ${naiveDepth(shallow)}, deep depth ${deepDepth(shallow)}, sum ${deepSum(shallow)}")

    val hostile = nestedChain(depth = 200_000)
    println("naive on 200k levels: ${runCatching { naiveDepth(hostile) }.exceptionOrNull()?.javaClass?.simpleName}")
    println("DeepRecursiveFunction on 200k levels: ${deepDepth(hostile)}")
    println(runCatching { requireMaxDepth(hostile, maxDepth = 64) }.exceptionOrNull()?.message)
}
