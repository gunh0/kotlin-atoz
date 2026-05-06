package atoz.basics

import java.util.Locale

/*
Case conversion and formatting depend on the JVM default locale (the server's).
- Kotlin's `lowercase()`/`uppercase()` always use Locale.ROOT. Java's no-arg
  `toLowerCase()` uses the default locale: on a Turkish system "TITLE"
  becomes "tıtle" (dotless ı), so a check like `role.toLowerCase() == "title"`
  passes on one server and fails on another. Pass a locale only for text shown
  to users: `lowercase(Locale.forLanguageTag("tr"))`.
- Even locale-free case mapping isn't a safe identity check: "ADMİN" (capital
  I with dot) lowercases to "admi̇n" (6 chars), yet `equals("admin",
  ignoreCase = true)` is true. Restrict identifiers to ASCII first.
- `"%.2f".format(x)` also uses the default locale (1,50 in Germany): pass
  Locale.ROOT for machine-readable output. `padStart`/`padEnd` align columns
  and never truncate.
*/

private val usernameChars = Regex("[A-Za-z0-9._-]{3,32}")

fun canonicalUsername(raw: String): String? = raw.trim().takeIf { it.matches(usernameChars) }?.lowercase()

fun formatAmount(cents: Long, locale: Locale = Locale.ROOT): String = "%.2f".format(locale, cents / 100.0)

fun reportLine(name: String, cents: Long): String = name.padEnd(8, '.') + formatAmount(cents).padStart(10)

fun main() {
    val turkish = Locale.forLanguageTag("tr")
    println("TITLE: root=${"TITLE".lowercase()} tr=${"TITLE".lowercase(turkish)}")
    println("ADMİN lowercase=${"ADMİN".lowercase()} equalsIgnoreCase(admin)=${"ADMİN".equals("admin", ignoreCase = true)}")
    println("canonical: ${canonicalUsername(" Alice.Ops ")}, ${canonicalUsername("ADMİN")}")
    println("1.5 in Germany: ${formatAmount(150, Locale.GERMANY)}, root: ${formatAmount(150)}")
    println(reportLine("alice", 123_456))
    println(reportLine("bob", 99))
}
