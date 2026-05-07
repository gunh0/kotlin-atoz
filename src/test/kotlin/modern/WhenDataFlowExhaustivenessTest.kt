package atoz.modern

import kotlin.test.Test
import kotlin.test.assertEquals

class WhenDataFlowExhaustivenessTest {
    @Test
    fun earlyReturnCountsTowardsExhaustiveness() {
        assertEquals(0, processingFeeCents(PaymentMethod.Voucher, 10_000))
        assertEquals(200, processingFeeCents(PaymentMethod.Card("4111", international = false), 10_000))
        assertEquals(300, processingFeeCents(PaymentMethod.Card("4111", international = true), 10_000))
        assertEquals(25, processingFeeCents(PaymentMethod.BankTransfer("DE89"), 10_000))
    }

    @Test
    fun worksForEnumsToo() {
        assertEquals(
            listOf("auto-approve", "manual review", "block and alert"),
            RiskLevel.entries.map(::reviewQueue),
        )
    }

    @Test
    fun returnInsideAnExpressionBody() {
        assertEquals("************1111", maskedCardNumber(PaymentMethod.Card("4111111111111111", false)))
        assertEquals("no card", maskedCardNumber(PaymentMethod.Voucher))
        assertEquals("no card", maskedCardNumber(null))
    }
}
