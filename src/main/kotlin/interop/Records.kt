package atoz.interop

import java.nio.file.attribute.GroupPrincipal
import java.nio.file.attribute.UserPrincipal
import jdk.net.UnixDomainPrincipal

/*
Java records and Kotlin data classes solve the same problem.
- A Java record used from Kotlin: its components read as properties
  (`principal.user` calls `user()`), and equals/hashCode/toString are Java's.
  The JDK itself ships a few, e.g. jdk.net.UnixDomainPrincipal.
- A Kotlin `data class` is an ordinary final class to Java, with getX()
  accessors. Annotating it with @JvmRecord compiles it to a real
  java.lang.Record (needs JVM target 16+): accessors become `x()`, reflection
  sees record components, and Java code can pattern-match on it.
@JvmRecord requires all properties in the primary constructor and only `val`s,
because records are shallowly immutable.
*/

@JvmRecord
data class Coordinate(val lat: Double, val lon: Double)

data class PlainCoordinate(val lat: Double, val lon: Double)

fun describePrincipal(user: String, group: String): String {
    val principal = UnixDomainPrincipal(UserPrincipal { user }, GroupPrincipal { group })
    return "${principal.user.name}:${principal.group.name}" // record components as properties
}

fun main() {
    println("Coordinate is a record: ${Coordinate::class.java.isRecord}")
    println("components: ${Coordinate::class.java.recordComponents.map { it.name }}")
    println("PlainCoordinate is a record: ${PlainCoordinate::class.java.isRecord}")
    println(Coordinate(37.56, 126.97))
    println(describePrincipal("alice", "staff"))
}
