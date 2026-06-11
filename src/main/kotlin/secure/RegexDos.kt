package atoz.secure

/*
ReDoS: Kotlin's Regex is java.util.regex, a backtracking engine, and the input
is attacker-chosen. Since Java 9 the engine memoizes simple group loops, so the
textbook `^(a+)+$` is "only" quadratic, but ambiguous patterns still explode:
`^(.*,){8}x$` tries every way to cut a run of commas into 8 fields, roughly
n^8 steps; 40 commas are enough to burn a CPU core.
- Bound the input length first: quadratic is harmless at 256 chars, not at 1 MB.
- Remove the ambiguity: in `(?:[^,]*,){8}` a field can't swallow the next
  comma, so there is nothing to backtrack into. Possessive quantifiers and
  atomic groups (`a++`, `(?>a+)`) forbid backtracking outright.
- For patterns you don't control, give the matcher a step budget: the engine
  reads input only through CharSequence.get, so a wrapper that counts reads
  aborts a runaway match deterministically, without threads or timeouts.
*/

class RegexBudgetExceeded(steps: Int) : RuntimeException("regex gave up after $steps character reads")

object BoundedRegex {
    const val MAX_INPUT = 256

    fun matches(regex: Regex, input: String, maxSteps: Int = 100_000): Boolean {
        require(input.length <= MAX_INPUT) { "input longer than $MAX_INPUT characters" }
        return regex.matches(CountingChars(input, StepBudget(maxSteps)))
    }

    // How many character reads a full match attempt takes (throws past the budget).
    fun stepsFor(regex: Regex, input: String, maxSteps: Int = 100_000): Int {
        val budget = StepBudget(maxSteps)
        regex.matches(CountingChars(input, budget))
        return budget.used
    }

    private class StepBudget(private val max: Int) {
        var used = 0
            private set

        fun spend() {
            if (++used > max) throw RegexBudgetExceeded(max)
        }
    }

    private class CountingChars(private val text: CharSequence, private val budget: StepBudget) : CharSequence {
        override val length: Int get() = text.length

        override fun get(index: Int): Char {
            budget.spend()
            return text[index]
        }

        override fun subSequence(startIndex: Int, endIndex: Int): CharSequence =
            CountingChars(text.subSequence(startIndex, endIndex), budget)

        override fun toString(): String = text.toString()
    }
}

fun main() {
    val commas = ",".repeat(40) + "!"
    for (pattern in listOf("^(.*,){8}x$", "^(?:[^,]*,){8}x$")) {
        val outcome = runCatching { BoundedRegex.stepsFor(Regex(pattern), commas) }
        println("$pattern on 40 commas -> ${outcome.fold({ "$it reads" }, { it.message })}")
    }
    for (length in listOf(64, 128, 256)) {
        println("^(a+)+$ on $length chars -> ${BoundedRegex.stepsFor(Regex("^(a+)+$"), "a".repeat(length) + "!")} reads")
    }
    println(runCatching { BoundedRegex.matches(Regex("^a+$"), "a".repeat(10_000)) }.exceptionOrNull()?.message)
}
