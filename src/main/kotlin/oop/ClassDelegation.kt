package atoz.oop

// `class A(impl: I) : I by impl` implements interface I by forwarding every
// member to impl; the compiler writes the boilerplate Java's decorators need.
// You override only what you want to change. This is composition, so it avoids
// the fragile base class problem of Effective Java's InstrumentedHashSet:
// subclassing HashSet and counting in both add() and addAll() double-counts,
// because HashSet.addAll() calls add().
//
// The flip side: forwarded calls go straight to the delegate and never come
// back through your overrides, so addAll() must be overridden separately.

class CountingSet<T>(private val inner: MutableSet<T> = HashSet()) : MutableSet<T> by inner {
    var attempted = 0
        private set

    override fun add(element: T): Boolean {
        attempted++
        return inner.add(element)
    }

    override fun addAll(elements: Collection<T>): Boolean {
        attempted += elements.size
        return inner.addAll(elements)
    }
}

interface Repository {
    fun find(id: Int): String?
    fun save(id: Int, value: String)
}

class InMemoryRepository : Repository {
    private val data = mutableMapOf<Int, String>()
    override fun find(id: Int): String? = data[id]
    override fun save(id: Int, value: String) {
        data[id] = value
    }
}

class ReadOnlyGuard(private val inner: Repository) : Repository by inner {
    override fun save(id: Int, value: String) {
        throw UnsupportedOperationException("read-only replica")
    }
}

fun main() {
    val set = CountingSet<String>()
    set.add("a")
    set.addAll(listOf("a", "b", "c"))
    println("size=${set.size} attempted=${set.attempted} contains b: ${"b" in set}")

    val primary = InMemoryRepository().apply { save(1, "alice") }
    val replica = ReadOnlyGuard(primary)
    println("replica.find(1) = ${replica.find(1)}")
    println("replica.save -> ${runCatching { replica.save(2, "bob") }.exceptionOrNull()}")
}
