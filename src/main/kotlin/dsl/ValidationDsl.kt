package atoz.dsl

import kotlin.reflect.KProperty1

/*
A declarative validation DSL: rules are attached to properties through
property references (`Registration::email`), so a renamed field is a compile
error instead of a stale string, and the field name in each violation comes
from the reference itself (`.name` and `.get()` need no kotlin-reflect).
Rule helpers are extension functions on `FieldRules<String>` or
`FieldRules<Int>`, so `notBlank()` is offered for a String field and simply
doesn't exist for an Int one. That is the type safety a Java Bean
Validation annotation (`@NotBlank` on an int compiles fine) can't give.
`validate` runs every rule and returns all violations, which is what a form
or an API error response needs; nothing throws for invalid input.
*/

data class Violation(val field: String, val message: String)

@DslMarker
annotation class ValidationMarker

@ValidationMarker
class FieldRules<R> internal constructor() {
    internal val checks = mutableListOf<Pair<String, (R) -> Boolean>>()

    fun rule(message: String, predicate: (R) -> Boolean) {
        checks += message to predicate
    }
}

fun FieldRules<String>.notBlank() = rule("must not be blank") { it.isNotBlank() }

fun FieldRules<String>.lengthIn(range: IntRange) = rule("length must be in $range") { it.length in range }

fun FieldRules<String>.matches(pattern: Regex, description: String) = rule("must be $description") { pattern.matches(it) }

fun FieldRules<Int>.atLeast(minimum: Int) = rule("must be at least $minimum") { it >= minimum }

@ValidationMarker
class Validator<T> internal constructor() {
    private val fieldChecks = mutableListOf<(T) -> List<Violation>>()

    fun <R> field(property: KProperty1<T, R>, configure: FieldRules<R>.() -> Unit) {
        val rules = FieldRules<R>().apply(configure).checks.toList()
        fieldChecks += { subject ->
            val value = property.get(subject)
            rules.filterNot { (_, predicate) -> predicate(value) }.map { (message, _) -> Violation(property.name, message) }
        }
    }

    fun validate(subject: T): List<Violation> = fieldChecks.flatMap { it(subject) }
}

fun <T> validator(configure: Validator<T>.() -> Unit): Validator<T> = Validator<T>().apply(configure)

data class Registration(val username: String, val email: String, val age: Int)

val registrationRules = validator<Registration> {
    field(Registration::username) {
        notBlank()
        lengthIn(3..32)
        matches(Regex("[a-z0-9_]+"), "lowercase letters, digits or _")
    }
    field(Registration::email) {
        matches(Regex("[^@\\s]+@[^@\\s]+\\.[a-z]{2,}"), "an email address")
    }
    field(Registration::age) {
        atLeast(16)
        rule("must be a plausible age") { it <= 150 }
    }
}

fun main() {
    println(registrationRules.validate(Registration("alice_01", "alice@example.com", 30)))
    registrationRules.validate(Registration("Al", "not-an-email", 12)).forEach { println("${it.field}: ${it.message}") }
}
