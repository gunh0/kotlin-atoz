package atoz.functional

import kotlin.test.Test
import kotlin.test.assertEquals

class TypedErrorsTest {
    @Test
    fun parsesAmountsIntoCents() {
        assertEquals(Outcome.Ok(12_550L), parseCents("125.50"))
        assertEquals(Outcome.Ok(1_000L), parseCents(" 10 "))
        assertEquals(Outcome.Err(TransferError.InvalidAmount("12,5")), parseCents("12,5"))
        assertEquals(Outcome.Err(TransferError.InvalidAmount("-3")), parseCents("-3"))
        assertEquals(Outcome.Err(TransferError.InvalidAmount("0.00")), parseCents("0.00"))
    }

    @Test
    fun happyPathRunsEveryStep() {
        assertEquals(Outcome.Ok(TransferOrder("a", "b", 2_500)), planTransfer("a", "b", "25"))
    }

    @Test
    fun firstFailureShortCircuitsTheChain() {
        // invalid amount and same account: only the first error is reported
        assertEquals(Outcome.Err(TransferError.InvalidAmount("abc")), planTransfer("a", "a", "abc"))
        assertEquals(Outcome.Err(TransferError.OverLimit(1_000_000)), planTransfer("a", "a", "50000"))
        assertEquals(Outcome.Err(TransferError.SameAccount), planTransfer("a", "a", "10"))
    }

    @Test
    fun mapTouchesOnlyOkAndFoldHandlesBoth() {
        assertEquals(Outcome.Ok(10L), planTransfer("a", "b", "10").map { it.cents / 100 })
        val err: Outcome<TransferError, TransferOrder> = Outcome.Err(TransferError.SameAccount)
        assertEquals<Outcome<TransferError, Any>>(err, err.map { it.cents * 2 }) // Err passes through untouched
        assertEquals("rejected: same account", describeTransfer(err))
        assertEquals("rejected: above the limit of 10000", describeTransfer(planTransfer("a", "b", "10000.01")))
        assertEquals("ok: 1050 cents from a to b", describeTransfer(planTransfer("a", "b", "10.50")))
    }
}
