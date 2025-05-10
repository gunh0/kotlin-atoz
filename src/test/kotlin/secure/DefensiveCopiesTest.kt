package atoz.secure

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class DefensiveCopiesTest {
    @Test
    fun naiveGrantChangesAfterValidation() {
        val requested = mutableListOf("reader")
        val grant = NaiveGrant("mallory@dev", requested)
        requested += "admin"
        assertTrue(grant.can("admin"))
    }

    @Test
    fun copiedGrantIsAStableSnapshot() {
        val requested = mutableListOf("reader")
        val grant = Grant.of("mallory@dev", requested)
        requested += "admin"
        assertFalse(grant.can("admin"))
        assertEquals(setOf("reader"), grant.roles)
    }

    @Test
    fun factoryValidatesTheCopy() {
        assertFailsWith<IllegalArgumentException> { Grant.of("mallory@dev", listOf("admin")) }
        assertFailsWith<IllegalArgumentException> { Grant.of("x", listOf("root")) }
        assertTrue(Grant.of("alice@ops", listOf("admin")).can("admin"))
    }

    @Test
    fun arraysInDataClassesCompareByIdentity() {
        val bytes = byteArrayOf(1, 2)
        assertNotEquals(KeyMaterial(byteArrayOf(1, 2)), KeyMaterial(byteArrayOf(1, 2)))
        val key = KeyMaterial(bytes)
        bytes[0] = 9
        assertEquals(9, key.bytes[0]) // shared, not copied
    }
}
