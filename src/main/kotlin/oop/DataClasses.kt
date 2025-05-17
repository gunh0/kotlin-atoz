package atoz.oop

// A data class generates equals(), hashCode(), toString(), copy() and
// component1()..componentN() from the properties in its primary constructor;
// Java records are the closest equivalent. Properties declared in the body take
// no part in any of them.
//
// copy() is a shallow copy with named overrides, the idiomatic way to "modify"
// an immutable value. Security note: the generated toString() prints every
// constructor property, so a data class holding a password or token leaks it
// into logs and exception messages. Override toString() for such classes.

data class Coordinates(val lat: Double, val lon: Double) {
    var label: String = "" // body property: ignored by equals/hashCode/toString
}

data class Release(val name: String, val version: String, val tags: List<String> = emptyList())

data class LoginRequest(val username: String, val password: String)

data class SafeLoginRequest(val username: String, val password: String) {
    override fun toString(): String = "SafeLoginRequest(username=$username, password=***)"
}

fun main() {
    val a = Coordinates(37.56, 126.97).apply { label = "Seoul" }
    val b = Coordinates(37.56, 126.97).apply { label = "City Hall" }
    println("$a == $b: ${a == b}, same hash: ${a.hashCode() == b.hashCode()}")

    val release = Release("kotlin", "2.1.0")
    val next = release.copy(version = "2.1.10")
    println("$release -> $next")
    val (name, version) = next
    println("name=$name version=$version")

    println(LoginRequest("ian", "hunter2"))
    println(SafeLoginRequest("ian", "hunter2"))
}
