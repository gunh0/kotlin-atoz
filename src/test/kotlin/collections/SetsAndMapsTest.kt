package atoz.collections

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class SetsAndMapsTest {
    @Test
    fun setOperations() {
        assertEquals(setOf("read", "write"), permissionsOf(setOf("editor", "viewer")))
        assertEquals(setOf("read"), permissionsOf(setOf("viewer", "unknown-role")))
        assertEquals(setOf("delete"), missingPermissions(setOf("viewer"), setOf("read", "delete")))
        assertEquals(setOf("read", "write"), sharedPermissions("editor", "admin"))
    }

    @Test
    fun missingKeys() {
        assertNull(rolePermissions["root"])
        assertFailsWith<NoSuchElementException> { rolePermissions.getValue("root") }
        assertEquals(emptySet(), rolePermissions.getOrElse("root") { emptySet() })
    }

    @Test
    fun plusReturnsANewMap() {
        val extended = rolePermissions + ("auditor" to setOf("audit"))
        assertEquals(4, extended.size)
        assertEquals(3, rolePermissions.size)
        assertEquals(setOf("viewer", "editor"), (rolePermissions - "admin").keys)
    }

    @Test
    fun transformationsAndGetOrPut() {
        assertEquals(mapOf("viewer" to 1, "editor" to 2, "admin" to 4), permissionCounts())
        assertEquals(
            mapOf('a' to listOf("apple", "avocado"), 'b' to listOf("banana")),
            indexByFirstLetter(listOf("apple", "avocado", "banana")),
        )
    }
}
