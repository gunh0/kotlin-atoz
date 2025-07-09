package atoz.basics

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class ExceptionsTest {
    @Test
    fun tryIsAnExpression() {
        assertEquals(8080, parsePortOr(" 8080 ", 80))
        assertEquals(80, parsePortOr("http", 80))
        assertNull(parsePortOrNull("http"))
    }

    @Test
    fun preconditionsPickTheExceptionType() {
        val wallet = Wallet(100)
        val bad = assertFailsWith<IllegalArgumentException> { wallet.withdraw(-5) }
        assertEquals("amount must be positive, was -5", bad.message)
        assertFailsWith<IllegalStateException> { wallet.withdraw(500) }
        wallet.withdraw(30)
        assertEquals(70, wallet.balance)
        wallet.close()
        assertEquals("wallet is closed", assertFailsWith<IllegalStateException> { wallet.withdraw(1) }.message)
    }

    @Test
    fun useClosesEvenOnFailure() {
        val ok = mutableListOf<String>()
        assertEquals("data", readAndClose(ok, fail = false))
        assertEquals(listOf("read", "closed"), ok)

        val failed = mutableListOf<String>()
        assertFailsWith<IllegalStateException> { readAndClose(failed, fail = true) }
        assertEquals(listOf("read", "closed"), failed)
    }
}
