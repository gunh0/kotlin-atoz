package atoz.oop

// Classes and members are final by default; `open` allows subclassing and
// overriding (Effective Java's "design for inheritance or prohibit it" as the
// default). An abstract class is implicitly open, can't be instantiated, and,
// unlike an interface, can hold state and take constructor parameters.
//
// The template method pattern fits well: a final public method fixes the
// algorithm, protected abstract members fill in the steps. `final override`
// stops further overriding of a member a subclass has overridden.

abstract class Report(val title: String) {
    private var renders = 0

    fun render(): String {
        renders++
        return listOf(header(), body(), footer()).joinToString("\n")
    }

    fun renderCount(): Int = renders

    protected open fun header(): String = "# $title"
    protected abstract fun body(): String
    protected open fun footer(): String = "-- end --"
}

open class FindingsReport(private val findings: List<String>) : Report("Findings") {
    override fun body(): String = findings.joinToString("\n") { "- $it" }
    final override fun footer(): String = "-- ${findings.size} finding(s) --"
}

class ExecutiveReport(findings: List<String>) : FindingsReport(findings) {
    override fun header(): String = "# Executive summary"
}

fun main() {
    val report = FindingsReport(listOf("SQL injection in /search", "Missing HSTS"))
    println(report.render())
    println()
    println(ExecutiveReport(listOf("1 critical")).render())
}
