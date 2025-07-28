package atoz.basics

// `is` replaces Java's instanceof and smart-casts on success, so there is no
// separate cast afterwards (Java 16's pattern matching gets close). `!is` is
// the negation, and a smart cast also flows through `&&`, `||` and early
// returns.
//
// `as` is an unsafe cast that throws ClassCastException; `as?` is the safe
// cast that yields null instead, and pairs well with `?:`. Casting null to a
// non-null type with `as` throws NullPointerException.
// Generic type arguments are erased at runtime, so you check `is List<*>`,
// not `is List<String>`.

fun describeValue(value: Any?): String = when (value) {
    null -> "null"
    is String -> "string of length ${value.length}"
    is Int -> "int, doubled ${value * 2}"
    is List<*> -> "list of ${value.size}"
    else -> "something else: ${value::class.simpleName}"
}

fun isNonBlankString(value: Any?): Boolean = value is String && value.isNotBlank()

fun lengthIfString(value: Any?): Int? = (value as? String)?.length

fun portFrom(value: Any): Int = value as? Int ?: (value as? String)?.toIntOrNull() ?: -1

fun forceString(value: Any?): String = value as String

fun main() {
    listOf("abc", 21, listOf(1, 2), 3.5, null).forEach { println(describeValue(it)) }
    println("isNonBlankString(\"  \") = ${isNonBlankString("  ")}")
    println("lengthIfString(42) = ${lengthIfString(42)}")
    println("portFrom(8080) = ${portFrom(8080)}, portFrom(\"443\") = ${portFrom("443")}, portFrom(1.5) = ${portFrom(1.5)}")
    println("forceString(42) -> ${runCatching { forceString(42) }.exceptionOrNull()}")
}
