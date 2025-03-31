package atoz.basics

// `if` and `when` are expressions: they produce a value, so Kotlin needs no
// ternary operator (`cond ? a : b` is `if (cond) a else b`). Used as an
// expression, `if` must have an `else` branch.
//
// `when` replaces Java's switch without fall-through. A branch can match
// several constants, a range (`in`), a type (`is`) or, without a subject, any
// boolean condition. As an expression it must be exhaustive, usually through
// `else`. `when (val x = ...)` binds the subject to a name scoped to the when.

fun maxOfTwo(a: Int, b: Int): Int = if (a > b) a else b

fun statusText(code: Int): String = when (code) {
    200, 204 -> "success"
    in 300..399 -> "redirect"
    401, 403 -> "denied"
    404 -> "not found"
    in 500..599 -> "server error"
    else -> "unexpected"
}

fun passwordStrength(password: String): String = when {
    password.length < 8 -> "weak"
    password.any(Char::isDigit) && password.any { !it.isLetterOrDigit() } -> "strong"
    else -> "medium"
}

fun describeInput(input: String): String = when (val n = input.toIntOrNull()) {
    null -> "not a number: $input"
    0 -> "zero"
    else -> if (n > 0) "positive $n" else "negative $n"
}

fun main() {
    println("max(3, 7) = ${maxOfTwo(3, 7)}")
    listOf(200, 302, 403, 404, 503, 999).forEach { println("$it -> ${statusText(it)}") }
    listOf("abc", "abcdefgh", "abcdefg1!").forEach { println("$it -> ${passwordStrength(it)}") }
    listOf("42", "0", "-5", "x").forEach { println(describeInput(it)) }
}
