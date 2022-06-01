package com.example.mod01

/*
In Kotlin, everything is an object in the sense that you can call member functions and properties on any variable.
Some types can have a special internal representation
– for example, numbers, characters and booleans can be represented as primitive values at runtime
– but to the user they look like ordinary classes.
 */

class Variable {
    var num1: Long = 10L
    val num2 = 20L

    var num3: Long? = 1_000L
//    num3 = null   // Expecting member declaration

    fun run() {
        println("var num1: $num1")
        println("val num2: $num2")

        println("var num3: $num3")
    }
}