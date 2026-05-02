package atoz.oop

/*
Implementing Comparable<T> gives a type a natural order, as in Java, but
Kotlin then lets it use operators and ranges:
- `a < b`, `a >= b` call `compareTo`; `v in min..max` works for any
  Comparable (`..` builds a ClosedRange); `maxOf`, `coerceIn`, `sorted()`.
- `compareValuesBy(a, b, sel1, sel2, ...)` compares field by field, the
  Kotlin version of Comparator.comparing(...).thenComparing(...).
Keep `compareTo` consistent with `equals` (0 exactly when equal), or sorted
and hashed collections disagree: Java's BigDecimal says 1.0 and 1.00 compare
equal but aren't `equals`, so a TreeSet keeps one and a HashSet both.
A `data class` gets `equals` from all constructor properties, so compare all
of them.
*/

data class SemVer(val major: Int, val minor: Int, val patch: Int, val preRelease: String? = null) : Comparable<SemVer> {
    // A pre-release sorts before its release: 2.0.0-rc1 < 2.0.0.
    override fun compareTo(other: SemVer): Int =
        compareValuesBy(this, other, { it.major }, { it.minor }, { it.patch }, { it.preRelease == null }, { it.preRelease })

    override fun toString() = "$major.$minor.$patch" + (preRelease?.let { "-$it" } ?: "")

    companion object {
        private val pattern = Regex("""(\d{1,6})\.(\d{1,6})\.(\d{1,6})(?:-([0-9A-Za-z.]{1,32}))?""")

        fun parse(text: String): SemVer {
            val match = requireNotNull(pattern.matchEntire(text)) { "not a semantic version: $text" }
            val (major, minor, patch, pre) = match.destructured
            return SemVer(major.toInt(), minor.toInt(), patch.toInt(), pre.ifEmpty { null })
        }
    }
}

// Clients outside the supported window must upgrade.
val supportedClients: ClosedRange<SemVer> = SemVer(1, 4, 0)..SemVer(2, 0, 0)

fun isSupportedClient(version: String): Boolean = SemVer.parse(version) in supportedClients

fun main() {
    val versions = listOf("2.0.0", "1.10.0", "1.9.3", "2.0.0-rc1", "1.4.0").map(SemVer::parse)
    println("sorted: ${versions.sorted()}")
    println("1.10.0 > 1.9.3: ${SemVer.parse("1.10.0") > SemVer.parse("1.9.3")} (as strings: ${"1.10.0" > "1.9.3"})")
    println("supported 1.3.9: ${isSupportedClient("1.3.9")}, 2.0.0-rc1: ${isSupportedClient("2.0.0-rc1")}")
    println("clamped: ${SemVer.parse("3.1.0").coerceIn(supportedClients)}")
}
