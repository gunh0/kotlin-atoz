package atoz.dsl

/*
Context parameters (stable in 2.4) as DSL capabilities. A function declared
`context(tx: Transaction) fun transfer(...)` can only be called where a
Transaction is in context, i.e. inside a `transaction { }` block; outside it
the call doesn't compile. A lambda with receiver (`Transaction.() -> Unit`)
would also do that, but it puts every member of Transaction into `this`
and allows only one receiver. Context parameters stay out of `this`, and a
function can require several capabilities at once: `transfer` needs both a
Transaction and an Actor, so it is unreachable without an authenticated
actor too. The block type `context(Transaction) () -> T` is a context
function type; the stdlib `context(value) { }` provides the value.
Java has no equivalent: the transaction would be a ThreadLocal or an extra
parameter threaded through every call.
*/

class Actor(val name: String)

data class PendingTransfer(val from: String, val to: String, val cents: Long, val actor: String)

class Transaction internal constructor(private val book: AccountBook) {
    internal val pending = mutableListOf<PendingTransfer>()

    internal fun balanceOf(account: String): Long =
        book.balanceOf(account) + pending.sumOf {
            (if (it.to == account) it.cents else 0) - (if (it.from == account) it.cents else 0)
        }
}

class AccountBook(initial: Map<String, Long>) {
    private val balances = initial.toMutableMap()
    private val committed = mutableListOf<String>()
    val auditTrail: List<String> get() = committed.toList()

    fun balanceOf(account: String): Long = balances[account] ?: 0

    // All-or-nothing: changes and audit entries are applied only if the whole block succeeds.
    fun <T> transaction(block: context(Transaction) () -> T): T {
        val tx = Transaction(this)
        val result = context(tx) { block() }
        for (t in tx.pending) {
            balances[t.from] = balanceOf(t.from) - t.cents
            balances[t.to] = balanceOf(t.to) + t.cents
            committed += "${t.actor}: ${t.from} -> ${t.to} ${t.cents}"
        }
        return result
    }
}

fun <T> asActor(actor: Actor, block: context(Actor) () -> T): T = context(actor) { block() }

context(tx: Transaction, actor: Actor)
fun transfer(from: String, to: String, cents: Long) {
    require(cents > 0) { "amount must be positive" }
    check(tx.balanceOf(from) >= cents) { "insufficient funds in $from" }
    tx.pending += PendingTransfer(from, to, cents, actor.name)
}

context(tx: Transaction)
fun pendingBalance(account: String): Long = tx.balanceOf(account)

fun main() {
    val book = AccountBook(mapOf("alice" to 100, "bob" to 0))
    asActor(Actor("teller-7")) {
        book.transaction {
            transfer("alice", "bob", 60)
            println("pending alice: ${pendingBalance("alice")}, committed alice: ${book.balanceOf("alice")}")
        }
        val failed = runCatching {
            book.transaction {
                transfer("alice", "bob", 30)
                transfer("alice", "bob", 30) // only 10 left: the whole block fails
            }
        }
        println("second transaction: ${failed.exceptionOrNull()?.message}")
    }
    println("alice=${book.balanceOf("alice")} bob=${book.balanceOf("bob")}")
    println(book.auditTrail)
}
