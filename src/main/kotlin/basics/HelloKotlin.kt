package atoz.basics

// The first program: top-level functions need no class, and `main` may take
// no parameters at all.
fun greeting(name: String = "Kotlin"): String = "Hello, $name!"

fun main(args: Array<String>) {
    println(greeting())
    println("Program arguments: ${args.joinToString()}")
}
