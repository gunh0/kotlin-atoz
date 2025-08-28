package atoz.basics

// Integer arithmetic wraps on overflow silently, exactly like Java:
// Int.MAX_VALUE + 1 == Int.MIN_VALUE. Use Long, or Math.addExact/multiplyExact
// when an overflow must be an error (lengths, sizes, money). Integer division
// by zero throws ArithmeticException; Double division gives Infinity or NaN.
// `%` keeps the sign of the dividend (-7 % 3 == -1); mod() is always
// non-negative for a positive divisor, which is what bucketing code wants.
//
// Unsigned types (UByte, UShort, UInt, ULong, literals with `u`) have no Java
// equivalent. They make byte handling natural: Byte is signed, so 0xFF is -1
// as a Byte, while toUByte() reads it as 255.

fun wrappingAdd(a: Int, b: Int): Int = a + b
fun checkedAdd(a: Int, b: Int): Int = Math.addExact(a, b)
fun safeTotal(a: Int, b: Int): Long = a.toLong() + b

fun intDivide(a: Int, b: Int): Int = a / b
fun doubleDivide(a: Double, b: Double): Double = a / b

fun remainder(a: Int, n: Int): Int = a % n
fun bucket(a: Int, n: Int): Int = a.mod(n)

fun signedByte(value: Int): Byte = value.toByte()
fun unsignedValue(b: Byte): Int = b.toUByte().toInt()

fun hexOf(bytes: ByteArray): String = bytes.joinToString("") { it.toUByte().toString(16).padStart(2, '0') }

fun main() {
    println("Int.MAX_VALUE + 1 = ${wrappingAdd(Int.MAX_VALUE, 1)}")
    println("addExact -> ${runCatching { checkedAdd(Int.MAX_VALUE, 1) }.exceptionOrNull()}")
    println("as Long: ${safeTotal(Int.MAX_VALUE, 1)}")
    println("1_000_000 * 3_000 as Long: ${1_000_000L * 3_000}")
    println("1 / 0 -> ${runCatching { intDivide(1, 0) }.exceptionOrNull()}, 1.0 / 0.0 = ${doubleDivide(1.0, 0.0)}")
    println("-7 % 3 = ${remainder(-7, 3)}, (-7).mod(3) = ${bucket(-7, 3)}")
    println("0xFF.toByte() = ${signedByte(0xFF)}, as unsigned = ${unsignedValue(signedByte(0xFF))}")
    println("UInt.MAX_VALUE = ${UInt.MAX_VALUE}, 0u - 1u = ${0u - 1u}")
    println("hex: ${hexOf(byteArrayOf(0x0A, -1, 0x7F))}")
    println("literals: 0xFF=${0xFF}, 0b1010=${0b1010}, 1_000_000=${1_000_000}")
}
