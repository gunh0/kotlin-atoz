package atoz.functional

// An extension function adds a method to a type you don't own without
// subclassing or a utils class: `fun String.maskedEmail()` is called as
// "a@b".maskedEmail(). It compiles to a static method taking the receiver as
// its first parameter, which has two consequences:
// - extensions are resolved statically, by the declared type of the
//   expression, not by the runtime type: no polymorphism;
// - a member with the same signature always wins over an extension.
// Extensions can have nullable receivers (`String?.orDash()`) and extension
// properties are allowed, but they can't have a backing field.

fun String.maskedEmail(): String {
    val at = indexOf('@')
    if (at <= 1) return this
    return first() + "*".repeat(at - 1) + substring(at)
}

fun String?.orDash(): String = if (isNullOrBlank()) "-" else this

val String.wordCount: Int
    get() = split(Regex("\\s+")).count { it.isNotEmpty() }

fun <T> List<T>.secondOrNull(): T? = if (size >= 2) this[1] else null

open class ExtShape
class ExtCircle : ExtShape()

fun ExtShape.kind(): String = "shape"
fun ExtCircle.kind(): String = "circle"

fun kindOf(shape: ExtShape): String = shape.kind() // static: uses ExtShape.kind()

class ExtGreeter {
    fun hello(): String = "member"
}

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
fun ExtGreeter.hello(): String = "extension"

fun main() {
    println("ian.park@example.com".maskedEmail())
    println("null.orDash() = ${null.orDash()}, \"x\".orDash() = ${"x".orDash()}")
    println("words: ${"  the quick  brown fox ".wordCount}")
    println("secondOrNull: ${listOf(1, 2, 3).secondOrNull()} / ${listOf(1).secondOrNull()}")
    val circle = ExtCircle()
    println("circle.kind() = ${circle.kind()}, kindOf(circle) = ${kindOf(circle)}")
    println("ExtGreeter().hello() = ${ExtGreeter().hello()}")
}
