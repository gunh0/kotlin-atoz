package atoz.basics

// Nullability is part of the type: String can never hold null, String? can.
// The compiler refuses `s.length` on a String? until you deal with null, which
// is how Kotlin removes most NullPointerExceptions at compile time.
//
// Tools: `?.` (safe call, yields null instead of throwing), `?:` (Elvis, a
// default when the left side is null; `?: return` and `?: throw` work too),
// `!!` (assert non-null, throws NPE: an explicit, greppable escape hatch), and
// smart casts: after `if (s != null)` the compiler treats s as String. Smart
// casts need a value that cannot change in between, so they work on locals and
// `val` properties, not on `var` properties; copy those to a local first.

fun lengthOrZero(s: String?): Int = s?.length ?: 0

fun shout(s: String?): String {
    if (s == null) return "(nothing)"
    return s.uppercase() // smart cast: s is String here
}

fun requireUsername(raw: String?): String =
    raw?.trim()?.takeIf { it.isNotEmpty() } ?: throw IllegalArgumentException("username required")

fun forcedLength(s: String?): Int = s!!.length

class NullableHolder(var value: String?) {
    fun describe(): String {
        val current = value // `value` is a var property: copy it before checking
        return if (current != null) "holds ${current.length} chars" else "empty"
    }
}

// A list that may contain nulls is not the same as a list that may be null.
fun countPresent(items: List<String?>): Int = items.filterNotNull().size
fun sizeOrMinusOne(items: List<String>?): Int = items?.size ?: -1

fun main() {
    println("lengthOrZero(null) = ${lengthOrZero(null)}, lengthOrZero(\"abc\") = ${lengthOrZero("abc")}")
    println(shout(null) + " / " + shout("kotlin"))
    println("requireUsername(\"  ian \") = ${requireUsername("  ian ")}")
    println("requireUsername(\"  \") -> ${runCatching { requireUsername("  ") }.exceptionOrNull()}")
    println("forcedLength(null) -> ${runCatching { forcedLength(null) }.exceptionOrNull()}")
    println(NullableHolder("abc").describe() + " / " + NullableHolder(null).describe())
    println("present: ${countPresent(listOf("a", null, "b"))}, size of null list: ${sizeOrMinusOne(null)}")
}
