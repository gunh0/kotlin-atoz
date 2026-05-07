package atoz.modern

/*
Two 2.3 changes that make early exits and `when` work together:
- Data-flow-based exhaustiveness (stable in 2.3): the compiler remembers which
  sealed subtypes or enum entries an earlier `if (x is A) return` already
  handled, so a later `when (x)` only needs the remaining cases. Before 2.3 it
  demanded a branch for A as well, or an `else` that silently swallowed new
  subtypes added later.
- `return` in expression bodies (enabled by default in 2.3, needs an explicit
  return type): `fun f(x: T?): String = x?.name ?: return "none"`. Before, an
  expression body could not contain `return` at all.
Java 21's switch has no such data-flow tracking: a pattern switch must cover
every permitted subtype again.
*/

sealed interface PaymentMethod {
    data class Card(val number: String, val international: Boolean) : PaymentMethod
    data class BankTransfer(val iban: String) : PaymentMethod
    data object Voucher : PaymentMethod
}

enum class RiskLevel { LOW, MEDIUM, HIGH }

fun processingFeeCents(method: PaymentMethod, amountCents: Long): Long {
    if (method is PaymentMethod.Voucher) return 0
    return when (method) { // no Voucher branch and no else: still exhaustive
        is PaymentMethod.Card -> amountCents * (if (method.international) 3 else 2) / 100
        is PaymentMethod.BankTransfer -> 25
    }
}

fun reviewQueue(level: RiskLevel): String {
    if (level == RiskLevel.LOW) return "auto-approve"
    return when (level) {
        RiskLevel.MEDIUM -> "manual review"
        RiskLevel.HIGH -> "block and alert"
    }
}

// `return` inside an expression body: allowed because the return type is explicit.
fun maskedCardNumber(method: PaymentMethod?): String =
    (method as? PaymentMethod.Card)?.number?.takeLast(4)?.padStart(16, '*') ?: return "no card"

fun main() {
    val methods = listOf(PaymentMethod.Card("4111111111111111", false), PaymentMethod.BankTransfer("DE89..."), PaymentMethod.Voucher)
    for (m in methods) println("$m -> fee ${processingFeeCents(m, 10_000)} cents, card ${maskedCardNumber(m)}")
    RiskLevel.entries.forEach { println("$it -> ${reviewQueue(it)}") }
}
