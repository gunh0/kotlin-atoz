package atoz.basics

// Basic types: Int, Long, Double, Float, Boolean, Char, String. On the JVM a
// non-nullable Int compiles to a primitive `int`; Int? becomes a boxed Integer.
//
// Unlike Java there is no implicit widening: an Int is never silently turned
// into a Long or a Double, and `1 == 1L` does not even compile. Convert
// explicitly with toLong(), toDouble(), ... Literals carry their type (1 is Int,
// 1L Long, 1.0 Double, 1f Float), but an integer literal adapts to the expected
// type, so `val l: Long = 1` is fine while `val l: Long = someInt` is not.
// A Char is not a number: `.code` gives its code point, digitToInt() its value.

fun typeNameOf(value: Any): String = value::class.simpleName ?: "?"

// Int * Long is defined (it returns Long), but the Int must still be converted
// before it is stored in a Long variable or passed to a Long parameter.
fun bytesFor(megabytes: Int): Long = megabytes.toLong() * 1024 * 1024

// Int / Int truncates; make one side a Double to get a fractional result.
fun integerAverage(total: Int, count: Int): Int = total / count
fun exactAverage(total: Int, count: Int): Double = total.toDouble() / count

// Narrowing keeps the low bits, exactly like Java's (int) cast.
fun narrowToInt(value: Long): Int = value.toInt()

// Double -> Int rounds toward zero; NaN becomes 0.
fun truncate(value: Double): Int = value.toInt()

fun codeOf(c: Char): Int = c.code
fun digitOf(c: Char): Int = c.digitToInt()

fun main() {
    listOf(1, 1L, 1.0, 1f, true, 'k', "kotlin").forEach { println("$it is ${typeNameOf(it)}") }
    println("2048 MB = ${bytesFor(2048)} bytes")
    println("7 / 2 = ${integerAverage(7, 2)}, 7.toDouble() / 2 = ${exactAverage(7, 2)}")
    println("4_294_967_297L.toInt() = ${narrowToInt(4_294_967_297L)}")
    println("(-3.99).toInt() = ${truncate(-3.99)}")
    println("'7'.code = ${codeOf('7')}, '7'.digitToInt() = ${digitOf('7')}")
}
