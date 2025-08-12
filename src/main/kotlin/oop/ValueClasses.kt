package atoz.oop

// `@JvmInline value class` wraps exactly one value. The compiler erases the
// wrapper where it can, so at runtime a UserId is usually a bare long, yet at
// compile time UserId and OrderId are different types and can't be swapped.
// That turns "primitive obsession" bugs (passing an order id where a user id
// belongs, cents where dollars belong) into compile errors at no runtime cost.
//
// An init block validates once, so an EmailAddress instance is always valid.
// Equality is by value; value classes have no identity (=== is not allowed).
// They are boxed when used as a nullable, generic or interface type.

@JvmInline
value class UserId(val raw: Long)

@JvmInline
value class OrderId(val raw: Long)

@JvmInline
value class Cents(val amount: Long) {
    operator fun plus(other: Cents): Cents = Cents(Math.addExact(amount, other.amount))
    override fun toString(): String = "%d.%02d".format(amount / 100, amount % 100)
}

@JvmInline
value class EmailAddress(val value: String) {
    init {
        require(value.count { it == '@' } == 1 && !value.startsWith('@') && !value.endsWith('@')) {
            "invalid email address"
        }
    }

    val domain: String get() = value.substringAfter('@')
}

class OrderBook {
    private val orders = mutableMapOf<OrderId, Pair<UserId, Cents>>()

    fun place(order: OrderId, user: UserId, total: Cents) {
        orders[order] = user to total
    }

    // fun spentBy(user: UserId) cannot be called with an OrderId by mistake.
    fun spentBy(user: UserId): Cents =
        orders.values.filter { it.first == user }.fold(Cents(0)) { acc, (_, total) -> acc + total }
}

fun main() {
    val book = OrderBook()
    book.place(OrderId(1), UserId(42), Cents(1999))
    book.place(OrderId(2), UserId(42), Cents(501))
    book.place(OrderId(3), UserId(7), Cents(100))
    println("user 42 spent ${book.spentBy(UserId(42))}")
    // book.spentBy(OrderId(1)) // does not compile: type mismatch
    println("UserId(1) == UserId(1): ${UserId(1) == UserId(1)}")
    println("domain: ${EmailAddress("ian@example.com").domain}")
    println("EmailAddress(\"nope\") -> ${runCatching { EmailAddress("nope") }.exceptionOrNull()}")
}
