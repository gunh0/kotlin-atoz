package atoz.dsl

/*
Lambdas with receiver are the building block of every Kotlin DSL.
A function type `Builder.() -> Unit` is a lambda that runs with a Builder as
`this`, so inside it you call the builder's members without a qualifier.
`buildString { append(...) }` and `apply { }` are standard examples.
Java has no equivalent; the closest is passing a Consumer<Builder> and
writing `b -> { b.append(...); }` with an explicit parameter everywhere.
A value of type `String.() -> Int` can be called both as `"abc".f()` and as
`f("abc")`: the receiver is just the first parameter.
*/

class EmailBuilder {
    private val recipients = mutableListOf<String>()
    var subject: String = ""
    private val body = StringBuilder()

    fun to(address: String) {
        recipients += address
    }

    fun line(text: String) {
        body.appendLine(text)
    }

    fun build(): String = "To: ${recipients.joinToString()}\nSubject: $subject\n\n$body".trimEnd()
}

// The whole "DSL": create a builder, run the caller's lambda with it as `this`, return the result.
fun email(configure: EmailBuilder.() -> Unit): String = EmailBuilder().apply(configure).build()

val vowelCount: String.() -> Int = { count { it in "aeiou" } }

fun main() {
    println(
        email {
            to("alice@example.com")
            to("bob@example.com")
            subject = "Weekly report"
            line("All scans passed.")
        },
    )
    println("kotlin".vowelCount() == vowelCount("kotlin"))
}
