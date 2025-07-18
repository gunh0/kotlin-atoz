package atoz.generics

// Type arguments are erased at runtime, so a normal generic function can't ask
// `x is T` or `T::class`; in Java you pass a Class<T> token instead. An inline
// function can declare its type parameter `reified`: the body is copied into
// each call site with the real type substituted, so `is T`, `as T`,
// `T::class` and `enumValues<T>()` all work.
//
// Reified parameters only exist on inline functions and need a type known at
// the call site; you can't call one with another function's plain T.

inline fun <reified T> List<Any?>.ofType(): List<T> = filterIsInstance<T>()

inline fun <reified T> simpleNameOf(): String = T::class.simpleName ?: "?"

inline fun <reified T> Any?.castOrNull(): T? = this as? T

inline fun <reified E : Enum<E>> parseEnumOrNull(raw: String): E? =
    enumValues<E>().firstOrNull { it.name.equals(raw.trim(), ignoreCase = true) }

enum class Algorithm { SHA256, SHA512, BLAKE2B }

inline fun <reified T> settingOf(settings: Map<String, Any>, key: String): T {
    val value = settings[key] ?: throw NoSuchElementException("missing setting: $key")
    return value as? T
        ?: throw IllegalArgumentException("setting $key is ${value::class.simpleName}, expected ${T::class.simpleName}")
}

fun main() {
    val mixed = listOf(1, "two", 3.0, "four", null)
    println("strings: ${mixed.ofType<String>()}, ints: ${mixed.ofType<Int>()}")
    println("simpleNameOf<Map<String, Int>>() = ${simpleNameOf<Map<String, Int>>()}")
    println("\"x\".castOrNull<Int>() = ${"x".castOrNull<Int>()}")
    println("parseEnumOrNull<Algorithm>(\"sha512\") = ${parseEnumOrNull<Algorithm>("sha512")}")
    val settings = mapOf("port" to 8080, "host" to "localhost")
    println("port: ${settingOf<Int>(settings, "port")}")
    println("host as Int -> ${runCatching { settingOf<Int>(settings, "host") }.exceptionOrNull()?.message}")
}
