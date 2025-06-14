package atoz.dsl

import kotlin.test.Test
import kotlin.test.assertEquals

class ScopeControlTest {
    @Test
    fun withoutMarkerTheOuterReceiverLeaks() {
        val t = looseTable {
            row {
                cell("a")
                row { cell("b") }
            }
        }
        // the nested call went to the table: two flat rows, the inner one registered first
        assertEquals(listOf(listOf("b"), listOf("a")), t.rows.map { it.cells })
    }

    @Test
    fun withMarkerOnlyExplicitOuterCallsAreAllowed() {
        val t = table {
            row { cell("a") }
            row {
                cell("b")
                this@table.row { cell("c") }
            }
        }
        assertEquals(listOf(listOf("a"), listOf("c"), listOf("b")), t.rows.map { it.cells })
    }
}
