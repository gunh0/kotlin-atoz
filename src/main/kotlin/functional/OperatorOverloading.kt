package atoz.functional

// Operators are calls to functions with fixed names marked `operator`:
// a + b -> a.plus(b), -a -> a.unaryMinus(), a[i] -> a.get(i),
// a[i] = v -> a.set(i, v), x in a -> a.contains(x), a(x) -> a.invoke(x),
// a < b -> a.compareTo(b) < 0, a += b -> a.plusAssign(b) (or a = a.plus(b)).
// Java reserves operators for built-in types; Kotlin lets BigDecimal, Duration,
// your own vectors or matrices read like math.
//
// Overload only where the meaning is obvious; `==` always maps to equals().

data class Vec2(val x: Int, val y: Int) : Comparable<Vec2> {
    operator fun plus(other: Vec2) = Vec2(x + other.x, y + other.y)
    operator fun minus(other: Vec2) = Vec2(x - other.x, y - other.y)
    operator fun times(scale: Int) = Vec2(x * scale, y * scale)
    operator fun unaryMinus() = Vec2(-x, -y)
    override fun compareTo(other: Vec2): Int = (x * x + y * y).compareTo(other.x * other.x + other.y * other.y)
}

class Grid(val width: Int, val height: Int) {
    private val cells = CharArray(width * height) { '.' }

    operator fun get(p: Vec2): Char = cells[index(p)]
    operator fun set(p: Vec2, value: Char) {
        cells[index(p)] = value
    }

    operator fun contains(p: Vec2): Boolean = p.x in 0..<width && p.y in 0..<height

    private fun index(p: Vec2): Int {
        require(p in this) { "out of bounds: $p" }
        return p.y * width + p.x
    }

    fun render(): String = cells.concatToString().chunked(width).joinToString("\n")
}

class Polynomial(private vararg val coefficients: Int) {
    operator fun invoke(x: Int): Int = coefficients.foldRight(0) { c, acc -> acc * x + c }
}

class Ledger {
    val entries = mutableListOf<Int>()
    operator fun plusAssign(amount: Int) {
        entries += amount
    }
}

fun main() {
    val a = Vec2(1, 2)
    val b = Vec2(3, 4)
    println("a + b = ${a + b}, b - a = ${b - a}, a * 3 = ${a * 3}, -a = ${-a}, a < b = ${a < b}")

    val grid = Grid(4, 2)
    grid[Vec2(1, 1)] = '#'
    println(grid.render())
    println("Vec2(5, 0) in grid: ${Vec2(5, 0) in grid}")

    val p = Polynomial(1, 0, 2) // 1 + 0x + 2x^2
    println("p(3) = ${p(3)}")

    val ledger = Ledger()
    ledger += 100
    ledger += -30
    println("ledger: ${ledger.entries}")
}
