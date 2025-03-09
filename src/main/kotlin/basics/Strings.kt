package atoz.basics

// String templates replace Java's concatenation and String.format for the
// common case: `$name` for a simple reference, `${expr}` for anything else.
// A literal dollar sign is written `\$` in an ordinary string.
//
// Raw strings ("""...""") keep backslashes and newlines as written, so regexes
// and Windows paths need no escaping. trimIndent() strips the common leading
// indentation; trimMargin() strips everything up to a `|`. A raw string has no
// escapes at all, so a literal `$` before an identifier is written `${'$'}`.
//
// buildString gives you a StringBuilder as the receiver and returns the result.

fun introduce(name: String, languages: List<String>): String =
    "$name writes ${languages.size} languages: ${languages.joinToString()}"

fun priceTag(dollars: Int): String = "\$$dollars"

val windowsPath: String = """C:\temp\new"""

val jsonSnippet: String = """
    {
      "lang": "kotlin",
      "price": "${'$'}0"
    }
""".trimIndent()

val marginText: String = """
    |first
    |  second (indented)
""".trimMargin()

fun csvLine(fields: List<String>): String = buildString {
    fields.forEachIndexed { index, field ->
        if (index > 0) append(',')
        if (',' in field || '"' in field) append('"').append(field.replace("\"", "\"\"")).append('"')
        else append(field)
    }
}

fun main() {
    println(introduce("gunh0", listOf("Kotlin", "Java", "Go")))
    println(priceTag(5))
    println("$windowsPath has ${windowsPath.length} characters")
    println(jsonSnippet)
    println(marginText)
    println(csvLine(listOf("id", "note, with comma", "say \"hi\"")))
}
