package atoz.secure

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StrictParsingTest {
    private fun rejection(raw: String): String = (InviteParser.parse(raw) as InviteParse.Rejected).reason

    @Test
    fun validInputIsAccepted() {
        assertEquals(
            InviteParse.Accepted(Invite("ana@example.com", InviteRole.EDITOR, 7)),
            InviteParser.parse("email=ana@example.com\n\nrole=editor\ndays=7\n"),
        )
    }

    @Test
    fun onlyAllowlistedRolesCanBeRequested() {
        assertEquals("role not allowed", rejection("email=ana@example.com\nrole=admin\ndays=7"))
        assertEquals("role not allowed", rejection("email=ana@example.com\nrole=ADMIN\ndays=7"))
        assertEquals("role not allowed", rejection("email=ana@example.com\nrole=Editor\ndays=7"))
    }

    @Test
    fun valueOfAcceptsEveryConstantAndEchoesInput() {
        assertEquals(InviteRole.ADMIN, lenientRole(" admin "))
        val error = assertFailsWith<IllegalArgumentException> { InviteRole.valueOf("<script>") }
        assertTrue("<script>" in error.message!!)
    }

    @Test
    fun unknownAndDuplicateFieldsAreRejected() {
        assertEquals("unknown field", rejection("email=ana@example.com\nrole=viewer\ndays=7\nisAdmin=true"))
        assertEquals("unknown field", rejection("email=ana@example.com\nrole viewer\ndays=7"))
        assertEquals("duplicate field", rejection("email=ana@example.com\nrole=viewer\nrole=editor\ndays=7"))
    }

    @Test
    fun numbersMustBePlainAsciiDigits() {
        assertEquals(5, "+5".toIntOrNull())
        assertEquals(3, "٣".toIntOrNull()) // ARABIC-INDIC DIGIT THREE
        for (days in listOf("+5", "٣", " 7", "07x", "0", "31", "9999999999")) {
            assertEquals("days must be 1..30", rejection("email=ana@example.com\nrole=viewer\ndays=$days"), days)
        }
    }

    @Test
    fun sizeIsCheckedFirstAndReasonsNeverEchoInput() {
        assertEquals("input too long", rejection("x".repeat(InviteParser.MAX_INPUT + 1)))
        val reason = rejection("email=<img src=x onerror=alert(1)>\nrole=viewer\ndays=7")
        assertEquals("invalid email", reason)
        assertFalse("<img" in reason)
    }
}
