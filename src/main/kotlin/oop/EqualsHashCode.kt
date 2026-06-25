package atoz.oop

/*
Writing equals/hashCode by hand, for classes that can't be data classes.
- Without overrides a class compares by identity (`==` falls back to `===`),
  as in Java: two equal-looking objects are different map keys.
- `==` is null-safe and calls `equals`; implement it with `other is T` (or a
  class check) and derive `hashCode` from the same properties.
- Only immutable properties belong in hashCode. Mutating a key after putting
  it into a HashSet/HashMap leaves it in the wrong bucket: `contains` and
  `remove` fail and the entry leaks.
- `is` checks in equals stay symmetric because Kotlin classes are final by
  default. Open the class and a subclass adding fields breaks `a == b` ⇔ `b == a`.
*/

class IdentityHost(val name: String, val port: Int)

class HostKey(name: String, val port: Int) {
    val name: String = name.lowercase() // DNS names are case-insensitive: normalize once

    override fun equals(other: Any?): Boolean = other is HostKey && name == other.name && port == other.port

    override fun hashCode(): Int = 31 * name.hashCode() + port

    override fun toString() = "$name:$port"
}

class MutableHostKey(var name: String, val port: Int) {
    override fun equals(other: Any?): Boolean = other is MutableHostKey && name == other.name && port == other.port

    override fun hashCode(): Int = 31 * name.hashCode() + port
}

// The symmetry trap, only possible because the base class is `open`.
open class OpenPoint(val x: Int, val y: Int) {
    override fun equals(other: Any?): Boolean = other is OpenPoint && x == other.x && y == other.y
    override fun hashCode(): Int = 31 * x + y
}

class ColoredPoint(x: Int, y: Int, val color: String) : OpenPoint(x, y) {
    override fun equals(other: Any?): Boolean = other is ColoredPoint && super.equals(other) && color == other.color
    override fun hashCode(): Int = 31 * super.hashCode() + color.hashCode()
}

fun main() {
    println("identity: ${IdentityHost("db", 5432) == IdentityHost("db", 5432)}")
    println("value: ${HostKey("DB.internal", 5432) == HostKey("db.internal", 5432)}")
    val blocked = hashSetOf(MutableHostKey("evil.example", 443))
    val key = blocked.first()
    key.name = "renamed.example"
    println("after mutation contains itself: ${key in blocked}, size ${blocked.size}")
    val p = OpenPoint(1, 2)
    val c = ColoredPoint(1, 2, "red")
    println("p == c: ${p == c}, c == p: ${c == p}")
}
