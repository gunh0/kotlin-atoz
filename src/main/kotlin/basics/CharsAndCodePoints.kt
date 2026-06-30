package atoz.basics

import java.text.Normalizer

/*
A Kotlin `Char` is a UTF-16 code unit, exactly like Java's `char`, and
`String.length` counts code units, not characters: "😀" has length 2.
- Cutting by index can split a surrogate pair and leave half an emoji; cut by
  code point (`offsetByCodePoints`) instead.
- `Char.isDigit()` and `String.toInt()` accept every Unicode digit: "١٢٣"
  (Arabic-Indic) parses to 123. When input must be ASCII, check `in '0'..'9'`.
  `'7'.digitToInt()` replaces Java's `Character.getNumericValue` / `c - '0'`.
- Visually identical strings can differ in code points: "ｆｕｌｌ" (fullwidth)
  or "ﬁle" (ligature). Normalize to NFKC before validating or comparing
  identifiers, then validate the normalized form.
*/

fun asciiNumberOrNull(text: String): Int? =
    text.takeIf { it.isNotEmpty() && it.length <= 9 && it.all { c -> c in '0'..'9' } }?.toInt()

fun truncateByCodePoints(text: String, maxCodePoints: Int): String =
    if (text.codePointCount(0, text.length) <= maxCodePoints) text
    else text.substring(0, text.offsetByCodePoints(0, maxCodePoints))

fun normalizedIdentifier(raw: String): String = Normalizer.normalize(raw, Normalizer.Form.NFKC).lowercase()

fun main() {
    val wave = "hi👋"
    println("length=${wave.length}, code points=${wave.codePointCount(0, wave.length)}")
    println("take(3) splits the emoji: ${wave.take(3).last().isHighSurrogate()}, by code points: ${truncateByCodePoints(wave, 3)}")
    println("\"١٢٣\".toInt() = ${"١٢٣".toInt()}, ascii only: ${asciiNumberOrNull("١٢٣")}")
    println("'7'.digitToInt() = ${'7'.digitToInt()}, 'A'.code = ${'A'.code}")
    println("NFKC: ${normalizedIdentifier("ｆｕｌｌ")} ${normalizedIdentifier("ﬁle")}")
}
