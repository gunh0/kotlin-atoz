package atoz.oop

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ValueClassesTest {
    @Test
    fun equalityIsByValue() {
        assertEquals(UserId(1), UserId(1))
        assertEquals("UserId(raw=1)", UserId(1).toString())
    }

    @Test
    fun distinctTypesKeepIdsApart() {
        val book = OrderBook()
        book.place(OrderId(1), UserId(42), Cents(1999))
        book.place(OrderId(2), UserId(42), Cents(501))
        book.place(OrderId(3), UserId(7), Cents(100))
        assertEquals(Cents(2500), book.spentBy(UserId(42)))
        assertEquals("25.00", book.spentBy(UserId(42)).toString())
    }

    @Test
    fun initBlockValidates() {
        assertEquals("example.com", EmailAddress("ian@example.com").domain)
        assertFailsWith<IllegalArgumentException> { EmailAddress("nope") }
        assertFailsWith<IllegalArgumentException> { EmailAddress("a@b@c") }
    }

    @Test
    fun arithmeticCanBeChecked() {
        assertFailsWith<ArithmeticException> { Cents(Long.MAX_VALUE) + Cents(1) }
    }
}
