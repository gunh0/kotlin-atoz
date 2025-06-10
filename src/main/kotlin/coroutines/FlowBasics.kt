package atoz.coroutines

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking

/*
Flow is an asynchronous stream: a suspend-friendly Sequence, or a Java Stream
whose elements may arrive over time. Flows are cold: the `flow { }` block does
nothing until a terminal operator (`collect`, `toList`, `first`, ...) runs it,
and it runs again from scratch for every collector.
Intermediate operators like `map` and `filter` only build a new Flow; they
take suspend lambdas, so each step may call other suspend functions.
Terminal operators that need fewer values (`first`, `take`) cancel the
producer as soon as they have enough.
*/

fun countdown(from: Int, log: MutableList<String>): Flow<Int> = flow {
    log += "started"
    for (i in from downTo 1) {
        delay(100)
        log += "emit $i"
        emit(i)
    }
}

fun evenSquares(vararg numbers: Int): Flow<Int> =
    flowOf(*numbers.toTypedArray())
        .filter { it % 2 == 0 }
        .map { it * it }

fun main() = runBlocking {
    val log = mutableListOf<String>()
    val flow = countdown(3, log)
    println("after building: $log")
    flow.collect { println("collected $it") }
    println("first only: ${flow.first()}, log: $log")
    println("even squares: ${evenSquares(1, 2, 3, 4).toList()}")
}
