package atoz.generics

import kotlin.reflect.KClass
import kotlin.reflect.typeOf

// Generics on the JVM are erased: at runtime a List<String> and a List<Int>
// are the same java.util.List, so `x is List<String>` doesn't compile (check
// `is List<*>`), and an unchecked cast `as List<String>` cannot be verified.
// That cast "succeeds" and the ClassCastException surfaces later, wherever an
// element is used as a String (heap pollution), far from the actual bug.
//
// typeOf<T>() with a reified T captures the full type, arguments included, as
// a KType: the Kotlin counterpart of Java's super type token trick
// (new TypeToken<List<String>>() {}). Libraries such as kotlinx.serialization
// and Ktor use it to keep the type information erasure throws away. Equality,
// classifier and arguments work with the plain stdlib; a pretty toString()
// needs kotlin-reflect.

fun sameRuntimeClass(): Boolean = ArrayList<String>().javaClass == ArrayList<Int>().javaClass

fun isListOfSomething(value: Any): Boolean = value is List<*>

@Suppress("UNCHECKED_CAST")
fun pretendStrings(values: List<Any>): List<String> = values as List<String> // unchecked: not verified

fun firstLength(values: List<String>): Int = values.first().length

inline fun <reified A, reified B> sameType(): Boolean = typeOf<A>() == typeOf<B>()

inline fun <reified T> typeArgumentClasses(): List<KClass<*>?> = typeOf<T>().arguments.map { it.type?.classifier as? KClass<*> }

inline fun <reified T> isNullableType(): Boolean = typeOf<T>().isMarkedNullable

fun main() {
    println("List<String> and List<Int> share a runtime class: ${sameRuntimeClass()}")
    val polluted = pretendStrings(listOf(1, 2))
    println("unchecked cast succeeded, size ${polluted.size}")
    println("using an element -> ${runCatching { firstLength(polluted) }.exceptionOrNull()?.javaClass?.simpleName}")
    println("List<String> vs List<Int> as KType: same = ${sameType<List<String>, List<Int>>()}")
    println("arguments of Map<String, List<Int>>: ${typeArgumentClasses<Map<String, List<Int>>>().map { it?.simpleName }}")
    println("String? nullable: ${isNullableType<String?>()}")
}
