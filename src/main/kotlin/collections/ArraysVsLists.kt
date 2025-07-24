package atoz.collections

// Array<T> is a Java array (Array<Int> is Integer[]); IntArray, ByteArray, ...
// are the primitive arrays (int[], byte[]). Arrays are fixed-size and always
// mutable, and unlike Java they are invariant: an Array<String> is not an
// Array<Any>, so Java's ArrayStoreException can't happen.
//
// Arrays keep Java's identity semantics: `==` compares references and
// toString() prints a type and hash code. Use contentEquals/contentToString.
// Prefer List in APIs; reach for arrays for performance-sensitive primitives,
// varargs, Java interop, and secrets: a ByteArray can be wiped with fill(0),
// an immutable String cannot.

fun arraysEqualByReference(a: IntArray, b: IntArray): Boolean = a == b
fun arraysEqualByContent(a: IntArray, b: IntArray): Boolean = a contentEquals b
fun listsEqual(a: List<Int>, b: List<Int>): Boolean = a == b

fun describeArray(a: IntArray): String = a.contentToString()

fun boxedVersusPrimitive(): Pair<String, String> =
    arrayOf(1, 2).javaClass.simpleName to intArrayOf(1, 2).javaClass.simpleName

class Secret(private val bytes: ByteArray) {
    fun <R> use(block: (ByteArray) -> R): R = try {
        block(bytes)
    } finally {
        bytes.fill(0)
    }
}

fun main() {
    val a = intArrayOf(1, 2, 3)
    val b = intArrayOf(1, 2, 3)
    println("a == b: ${arraysEqualByReference(a, b)}, contentEquals: ${arraysEqualByContent(a, b)}")
    println("lists: ${listsEqual(a.toList(), b.toList())}")
    println("toString: ${a.toString().substringBefore('@')}@..., contentToString: ${describeArray(a)}")
    println("boxed vs primitive: ${boxedVersusPrimitive()}")
    a[0] = 99
    println("arrays are mutable: ${describeArray(a)}")

    val raw = "hunter2".encodeToByteArray()
    val length = Secret(raw).use { it.size }
    println("used secret of length $length, afterwards: ${raw.contentToString()}")
}
