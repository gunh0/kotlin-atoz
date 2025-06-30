package atoz.basics

// `==` is structural equality: it calls equals() and is null-safe, so
// `a == b` is roughly `a?.equals(b) ?: (b === null)`. `===` is referential
// equality (same object), which is what Java's `==` means for objects. The
// classic Java bug of comparing strings with `==` simply doesn't exist.
//
// A class gets equals() from Any (identity) unless it overrides it; data
// classes generate it from the primary constructor properties.
//
// Floating point: when both sides are statically Double, `==` follows IEEE 754
// (NaN != NaN, -0.0 == 0.0). When they are typed as Any, equals() is used and
// the answers flip (NaN equals NaN, -0.0 does not equal 0.0), as in collections.

class SessionToken(val value: String)
data class UserKey(val tenant: String, val id: Long)

fun freshCopy(s: String): String = buildString { append(s) }

fun ieeeEquals(a: Double, b: Double): Boolean = a == b
fun boxedEquals(a: Any, b: Any): Boolean = a == b

fun main() {
    val literal = "kotlin"
    val built = freshCopy("kotlin")
    println("literal == built: ${literal == built}, literal === built: ${literal === built}")

    println("SessionToken(\"t\") == SessionToken(\"t\"): ${SessionToken("t") == SessionToken("t")}")
    println("UserKey(\"acme\", 1) == UserKey(\"acme\", 1): ${UserKey("acme", 1) == UserKey("acme", 1)}")

    val nothing: String? = null
    println("null == \"x\": ${nothing == "x"}, null == null: ${nothing == null}")

    println("-0.0 == 0.0 as Double: ${ieeeEquals(-0.0, 0.0)}, as Any: ${boxedEquals(-0.0, 0.0)}")
    println("NaN == NaN as Double: ${ieeeEquals(Double.NaN, Double.NaN)}, as Any: ${boxedEquals(Double.NaN, Double.NaN)}")
}
