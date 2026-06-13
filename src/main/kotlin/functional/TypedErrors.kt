package atoz.functional

/*
Typed errors: `Result<T>` (see ResultType.kt) can only carry a Throwable, so
callers can't see which failures to expect. A small sealed `Outcome<E, A>`
(called Either in Arrow, Scala and Haskell) puts the error type into the
signature: `planTransfer` returns `Outcome<TransferError, TransferOrder>`,
and an exhaustive `when` over TransferError forces callers to handle every
case. Java needs checked exceptions or a hand-written sealed interface plus
pattern matching for the same guarantee.
`flatMap` chains the steps "railway style": the first Err skips the rest
and is passed through unchanged. Exceptions stay for bugs and outages;
expected business failures become values.
*/

sealed interface Outcome<out E, out A> {
    data class Ok<out A>(val value: A) : Outcome<Nothing, A>

    data class Err<out E>(val error: E) : Outcome<E, Nothing>
}

inline fun <E, A, B> Outcome<E, A>.map(transform: (A) -> B): Outcome<E, B> = when (this) {
    is Outcome.Ok -> Outcome.Ok(transform(value))
    is Outcome.Err -> this
}

inline fun <E, A, B> Outcome<E, A>.flatMap(next: (A) -> Outcome<E, B>): Outcome<E, B> = when (this) {
    is Outcome.Ok -> next(value)
    is Outcome.Err -> this
}

inline fun <E, A, R> Outcome<E, A>.fold(onErr: (E) -> R, onOk: (A) -> R): R = when (this) {
    is Outcome.Ok -> onOk(value)
    is Outcome.Err -> onErr(error)
}

sealed interface TransferError {
    data class InvalidAmount(val raw: String) : TransferError

    data class OverLimit(val limitCents: Long) : TransferError

    data object SameAccount : TransferError
}

data class TransferOrder(val from: String, val to: String, val cents: Long)

fun parseCents(raw: String): Outcome<TransferError, Long> {
    val match = Regex("""(\d{1,9})(?:\.(\d{2}))?""").matchEntire(raw.trim())
        ?: return Outcome.Err(TransferError.InvalidAmount(raw))
    val (whole, fraction) = match.destructured
    val cents = whole.toLong() * 100 + (fraction.ifEmpty { "0" }).toLong()
    return if (cents > 0) Outcome.Ok(cents) else Outcome.Err(TransferError.InvalidAmount(raw))
}

fun planTransfer(from: String, to: String, rawAmount: String, limitCents: Long = 1_000_000): Outcome<TransferError, TransferOrder> =
    parseCents(rawAmount)
        .flatMap { cents -> if (cents <= limitCents) Outcome.Ok(cents) else Outcome.Err(TransferError.OverLimit(limitCents)) }
        .flatMap { cents -> if (from != to) Outcome.Ok(TransferOrder(from, to, cents)) else Outcome.Err(TransferError.SameAccount) }

// Exhaustive: adding a TransferError subtype breaks this until it is handled.
fun describeTransfer(outcome: Outcome<TransferError, TransferOrder>): String = outcome.fold(
    onErr = { error ->
        when (error) {
            is TransferError.InvalidAmount -> "rejected: '${error.raw}' is not an amount"
            is TransferError.OverLimit -> "rejected: above the limit of ${error.limitCents / 100}"
            TransferError.SameAccount -> "rejected: same account"
        }
    },
    onOk = { "ok: ${it.cents} cents from ${it.from} to ${it.to}" },
)

fun main() {
    println(describeTransfer(planTransfer("acc-1", "acc-2", "125.50")))
    println(describeTransfer(planTransfer("acc-1", "acc-2", "12,5")))
    println(describeTransfer(planTransfer("acc-1", "acc-2", "50000")))
    println(describeTransfer(planTransfer("acc-1", "acc-1", "10")))
    println(planTransfer("acc-1", "acc-2", "10").map { it.cents / 100 })
}
