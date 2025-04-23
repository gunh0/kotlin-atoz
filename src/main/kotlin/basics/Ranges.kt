package atoz.basics

// Ranges replace most index-based for loops. `a..b` is closed (b included),
// `a..<b` is half-open (b excluded, the old spelling is `until`), `downTo`
// counts backwards and `step` skips. A range is an object (IntRange,
// CharRange, ...), so you can test membership with `in`, ask for first/last,
// or turn it into a list.
//
// Progressions are normalised: `1..10 step 4` ends at 9, the last value it
// actually reaches. A range whose start is past its end is empty, it does not
// count down: `5..1` has no elements, you need `5 downTo 1`.

fun closed(): List<Int> = (1..5).toList()
fun halfOpen(): List<Int> = (1..<5).toList()
fun countdown(): List<Int> = (10 downTo 1 step 3).toList()
fun everyFourth(): IntProgression = 1..10 step 4
fun letters(): String = ('a'..'e').joinToString("")

fun isWorkingHour(hour: Int): Boolean = hour in 9..<18
fun isPrintableAscii(c: Char): Boolean = c in ' '..'~'
fun isProbability(p: Double): Boolean = p in 0.0..1.0

fun main() {
    println("1..5      = ${closed()}")
    println("1..<5     = ${halfOpen()}")
    println("10 downTo 1 step 3 = ${countdown()}")
    val p = everyFourth()
    println("1..10 step 4: first=${p.first} last=${p.last} step=${p.step} -> ${p.toList()}")
    println("'a'..'e'  = ${letters()}")
    println("5..1 is empty: ${(5..1).isEmpty()}")
    println("18 is a working hour: ${isWorkingHour(18)}")
    println("(1..100).sum() = ${(1..100).sum()}")
}
