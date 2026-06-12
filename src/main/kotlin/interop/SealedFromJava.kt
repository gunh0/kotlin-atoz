package atoz.interop

import java.lang.reflect.Modifier

/*
What Java sees of a Kotlin sealed hierarchy. With a JVM target of 17 or later
(this project targets 21) the compiler writes the JVM `PermittedSubclasses`
attribute, so a Kotlin sealed class or interface is a real Java sealed type:
`Class.isSealed()` is true and `getPermittedSubclasses()` lists the subclasses.
The subclasses are final by default (data classes, objects), which is exactly
what Java's exhaustiveness check needs. Java 21 code can therefore write
    switch (decision) { case Allow a -> ...; case Deny d -> ...; case Challenge c -> ... }
without a `default` branch, and gets a compile error when Kotlin adds a subtype.
An `open` subclass is neither final nor sealed, so Java treats it as
`non-sealed`: anything may extend it and exhaustiveness stops there.
*/

sealed interface AccessDecision {
    data class Allow(val user: String) : AccessDecision
    data class Deny(val reason: String) : AccessDecision
    data object Challenge : AccessDecision
}

sealed class AuthMethod {
    class Password : AuthMethod()
    open class Federated(val issuer: String) : AuthMethod()
}

fun permittedNames(type: Class<*>): List<String> = type.permittedSubclasses.orEmpty().map { it.simpleName }.sorted()

fun javaModifiers(type: Class<*>): String = when {
    type.isSealed -> "sealed"
    Modifier.isFinal(type.modifiers) -> "final"
    else -> "non-sealed"
}

fun main() {
    for (type in listOf(AccessDecision::class.java, AuthMethod::class.java)) {
        println("${type.simpleName}: isSealed=${type.isSealed}, permits ${permittedNames(type)}")
        type.permittedSubclasses.forEach { println("  ${it.simpleName} is ${javaModifiers(it)}") }
    }
}
