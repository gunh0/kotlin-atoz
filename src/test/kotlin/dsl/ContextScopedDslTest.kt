package atoz.dsl

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ContextScopedDslTest {
    private val teller = Actor("teller-7")

    @Test
    fun committedTransactionUpdatesBalancesAndAudit() {
        val book = AccountBook(mapOf("alice" to 100, "bob" to 0))
        val seenInside = asActor(teller) {
            book.transaction {
                transfer("alice", "bob", 60)
                pendingBalance("alice") to book.balanceOf("alice")
            }
        }
        assertEquals(40L to 100L, seenInside) // pending inside, not yet committed
        assertEquals(40, book.balanceOf("alice"))
        assertEquals(60, book.balanceOf("bob"))
        assertEquals(listOf("teller-7: alice -> bob 60"), book.auditTrail)
    }

    @Test
    fun failedBlockLeavesNothingBehind() {
        val book = AccountBook(mapOf("alice" to 50))
        asActor(teller) {
            assertFailsWith<IllegalStateException> {
                book.transaction {
                    transfer("alice", "bob", 30)
                    transfer("alice", "bob", 30)
                }
            }
            assertFailsWith<IllegalArgumentException> { book.transaction { transfer("alice", "bob", 0) } }
        }
        assertEquals(50, book.balanceOf("alice"))
        assertEquals(0, book.balanceOf("bob"))
        assertEquals(emptyList(), book.auditTrail)
    }

    @Test
    fun capabilitiesAreProvidedByTheBlocks() {
        val book = AccountBook(mapOf("a" to 10))
        // Only Transaction is needed here, no Actor.
        assertEquals(10L, book.transaction { pendingBalance("a") })
        // The actor comes from the innermost asActor block.
        asActor(Actor("outer")) {
            asActor(Actor("inner")) { book.transaction { transfer("a", "b", 1) } }
        }
        assertEquals(listOf("inner: a -> b 1"), book.auditTrail)
    }
}
