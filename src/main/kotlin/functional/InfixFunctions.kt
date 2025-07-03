package atoz.functional

// A member or extension function with one parameter can be marked `infix` and
// called without dot and parentheses: `a to b` is just `a.to(b)`. That is how
// the standard library spells `to`, `until`, `downTo`, `step`, `zip`,
// `union`, `matches` ...
//
// Bitwise operations are infix functions too, since Kotlin has no `<<`, `&`,
// `|` or `^`: shl, shr, ushr, and, or, xor, plus the inv() function. Infix
// calls have lower precedence than arithmetic, so `1 shl 2 + 1` is `1 shl 3`.

infix fun Int.percentOf(total: Int): Int = total * this / 100

data class Permission(val bits: Int) {
    infix fun and(other: Permission): Permission = Permission(bits and other.bits)
    infix fun or(other: Permission): Permission = Permission(bits or other.bits)
    infix fun allows(required: Permission): Boolean = (bits and required.bits) == required.bits

    companion object {
        val READ = Permission(0b100)
        val WRITE = Permission(0b010)
        val EXECUTE = Permission(0b001)
    }
}

infix fun String.redactedWith(mask: Char): String = mask.toString().repeat(length)

fun flagsDemo(): Map<String, Int> = mapOf(
    "1 shl 4" to (1 shl 4),
    "0b1100 and 0b1010" to (0b1100 and 0b1010),
    "0b1100 or 0b1010" to (0b1100 or 0b1010),
    "0b1100 xor 0b1010" to (0b1100 xor 0b1010),
    "-16 shr 2" to (-16 shr 2),
    "-16 ushr 28" to (-16 ushr 28),
    "0.inv()" to 0.inv(),
)

fun main() {
    println("15 percentOf 200 = ${15 percentOf 200}")
    val rw = Permission.READ or Permission.WRITE
    println("rw allows WRITE: ${rw allows Permission.WRITE}, allows EXECUTE: ${rw allows Permission.EXECUTE}")
    println("\"hunter2\" redactedWith '*' = ${"hunter2" redactedWith '*'}")
    flagsDemo().forEach { (expr, value) -> println("$expr = $value") }
    val pair = "user" to "ian"
    println("pair = $pair")
}
