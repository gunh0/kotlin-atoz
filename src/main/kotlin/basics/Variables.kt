package atoz.basics

/*
In Kotlin, everything is an object in the sense that you can call member functions and properties on any variable.
Some types can have a special internal representation
– for example, numbers, characters and booleans can be represented as primitive values at runtime
– but to the user they look like ordinary classes.
 */

class Variable {
    var num1: Long = 10L // mutable
    val num2 = 20L // read-only, type inferred as Long

    var num3: Long? = 1_000L // nullable: may also hold null

    fun describe(): List<String> = listOf(
        "var num1: $num1",
        "val num2: $num2",
        "var num3: $num3",
    )
}

fun main() {
    val v = Variable()
    v.describe().forEach(::println)
    v.num3 = null
    println("after num3 = null: ${v.num3}")
}
