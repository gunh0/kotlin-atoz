package atoz.oop

// Initialization order: primary constructor parameters, then property
// initializers and `init` blocks interleaved in the order they appear in the
// class body, then the body of a secondary constructor (if one was called).
// A superclass is fully run through that sequence before the subclass starts.
//
// The trap is the same as in Java: a base class that calls an open member
// during construction runs the override before the subclass has initialized
// its properties. A non-null String can then be observed as null, despite the
// type system. Keep constructors free of calls to open members.

class InitTrace(name: String, private val log: MutableList<String>) {
    val first = "first property ($name)".also { log += it }

    init {
        log += "first init"
    }

    val second = "second property".also { log += it }

    init {
        log += "second init"
    }

    constructor(log: MutableList<String>) : this("anonymous", log) {
        log += "secondary constructor body"
    }
}

open class InitBase(log: MutableList<String>) {
    init {
        log += "base init sees: ${describe()}"
    }

    open fun describe(): String = "base"
}

class InitDerived(log: MutableList<String>) : InitBase(log) {
    private val label: String = "derived"

    init {
        log += "derived init sees: ${describe()}"
    }

    override fun describe(): String = "label=$label"
}

fun main() {
    val log = mutableListOf<String>()
    InitTrace(log)
    log.forEach(::println)
    println("---")
    val log2 = mutableListOf<String>()
    InitDerived(log2)
    log2.forEach(::println)
}
