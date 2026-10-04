package atoz.interop

import java.util.Optional
import kotlin.jvm.optionals.getOrDefault
import kotlin.jvm.optionals.getOrElse
import kotlin.jvm.optionals.getOrNull
import kotlin.jvm.optionals.toList

/*
Java APIs report absence with Optional<T> (Stream.findFirst, ServiceLoader,
many repository interfaces); Kotlin uses a nullable T?. Convert at the
boundary with the kotlin.jvm.optionals extensions (stable since 1.8):
- `getOrNull()` turns an Optional into T? so `?.`, `?:` and smart casts apply;
  `getOrDefault(x)`, `getOrElse { }` (lazy) and `toList()` cover the rest.
- `Optional.ofNullable(x)` goes the other way for a Java API that wants one.
  `Optional.of(x)` with a nullable x doesn't compile, but a platform-typed
  null from Java gets through and throws NullPointerException at runtime.
- `optional.get()` on an empty Optional throws NoSuchElementException; it is
  Java's `!!`, so don't call it unchecked.
Keep Optional out of Kotlin signatures: it costs an allocation, and
`Optional<T>?` has three states instead of two.
*/

// A Java-style lookup: the Stream API hands back an Optional.
fun findAdminOptional(users: List<String>): Optional<String> =
    users.stream().filter { it.startsWith("admin-") }.findFirst()

fun findAdmin(users: List<String>): String? = findAdminOptional(users).getOrNull()

fun adminOrGuest(users: List<String>): String = findAdminOptional(users).getOrDefault("guest")

fun adminOrComputed(users: List<String>, fallback: () -> String): String = findAdminOptional(users).getOrElse(fallback)

fun adminsAsList(users: List<String>): List<String> = findAdminOptional(users).toList()

fun toOptional(value: String?): Optional<String> = Optional.ofNullable(value)

fun main() {
    val staff = listOf("alice", "admin-bob")
    println("admin: ${findAdmin(staff)}, none: ${findAdmin(listOf("carol"))}")
    println("or guest: ${adminOrGuest(listOf("carol"))}")
    println("length via ?.: ${findAdmin(staff)?.length ?: 0}")
    println("back to Java: ${toOptional(null)} ${toOptional("x")}")
    println(runCatching { findAdminOptional(listOf("carol")).get() }.exceptionOrNull())
}
