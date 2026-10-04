package atoz.interop

import java.util.Optional
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class OptionalInteropTest {
    private val staff = listOf("alice", "admin-bob", "admin-eve")
    private val noAdmins = listOf("carol")

    @Test
    fun getOrNullBridgesToNullableTypes() {
        assertEquals("admin-bob", findAdmin(staff))
        assertNull(findAdmin(noAdmins))
        assertEquals(0, findAdmin(noAdmins)?.length ?: 0)
    }

    @Test
    fun defaultsAndLazyFallbacks() {
        assertEquals("guest", adminOrGuest(noAdmins))
        assertEquals("admin-bob", adminOrGuest(staff))
        var calls = 0
        assertEquals("admin-bob", adminOrComputed(staff) { calls++; "computed" })
        assertEquals(0, calls) // not evaluated when a value is present
        assertEquals("computed", adminOrComputed(noAdmins) { calls++; "computed" })
        assertEquals(1, calls)
    }

    @Test
    fun toListHasZeroOrOneElement() {
        assertEquals(listOf("admin-bob"), adminsAsList(staff))
        assertEquals(emptyList(), adminsAsList(noAdmins))
    }

    @Test
    fun fromNullableToOptional() {
        assertEquals(Optional.empty(), toOptional(null))
        assertEquals(Optional.of("x"), toOptional("x"))
        // Optional.of(nullableString) does not compile; a platform-typed null from Java does:
        assertFailsWith<NullPointerException> { Optional.of(System.getProperty("atoz.unset.property")) }
    }

    @Test
    fun getOnEmptyIsJavasDoubleBang() {
        val error = assertFailsWith<NoSuchElementException> { findAdminOptional(noAdmins).get() }
        assertTrue(error.message!!.contains("No value present"))
    }
}
