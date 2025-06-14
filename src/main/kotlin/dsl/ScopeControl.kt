package atoz.dsl

/*
@DslMarker: without it, every outer receiver stays in scope inside nested
lambdas, so `row { row { } }` compiles. The inner `row` silently resolves to
the outer `table` receiver and adds a sibling row instead of failing.
Annotating the builder classes with an annotation that is itself marked
`@DslMarker` makes implicit receivers of the same DSL unavailable in nested
lambdas; the inner call then fails to compile with
"'fun row(...)' can't be called in this context by implicit receiver".
An explicit `this@table.row { }` remains possible when it is really intended.
*/

// Without a marker: the outer receiver leaks into nested lambdas.
class LooseTable {
    val rows = mutableListOf<LooseRow>()
    fun row(configure: LooseRow.() -> Unit) {
        rows += LooseRow().apply(configure)
    }
}

class LooseRow {
    val cells = mutableListOf<String>()
    fun cell(text: String) {
        cells += text
    }
}

fun looseTable(configure: LooseTable.() -> Unit) = LooseTable().apply(configure)

@DslMarker
annotation class TableDsl

@TableDsl
class Table {
    val rows = mutableListOf<TableRow>()
    fun row(configure: TableRow.() -> Unit) {
        rows += TableRow().apply(configure)
    }
}

@TableDsl
class TableRow {
    val cells = mutableListOf<String>()
    fun cell(text: String) {
        cells += text
    }
}

fun table(configure: Table.() -> Unit) = Table().apply(configure)

fun main() {
    val loose = looseTable {
        row {
            cell("a")
            row { cell("b") } // compiles, and quietly adds a second row to the table
        }
    }
    println("loose rows: ${loose.rows.map { it.cells }}")

    val strict = table {
        row {
            cell("a")
            // row { cell("b") } // does not compile: the Table receiver is hidden here
            this@table.row { cell("b") } // explicit is still allowed
        }
    }
    println("strict rows: ${strict.rows.map { it.cells }}")
}
