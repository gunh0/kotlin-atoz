package atoz.dsl

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse

class SqlDslTest {
    @Test
    fun valuesBecomePlaceholdersAndParameters() {
        val hostile = "x' OR '1'='1"
        val query = select("id", "email") {
            from("users")
            where {
                "email" eq hostile
                "failed_logins" gt 3
            }
        }
        assertEquals("SELECT id, email FROM users WHERE email = ? AND failed_logins > ?", query.sql)
        assertEquals(listOf(hostile, 3), query.parameters)
        assertFalse("'" in query.sql)
    }

    @Test
    fun groupsInListsOrderAndLimit() {
        val query = select("id") {
            from("users")
            where {
                "active" eq true
                anyOf {
                    "role" eq "admin"
                    "role" isIn listOf("auditor", "owner")
                }
            }
            orderBy("id")
            limit(10)
        }
        assertEquals("SELECT id FROM users WHERE active = ? AND (role = ? OR role IN (?, ?)) ORDER BY id LIMIT ?", query.sql)
        assertEquals(listOf(true, "admin", "auditor", "owner", 10), query.parameters)
    }

    @Test
    fun noWhereClauseWithoutConditions() {
        val query = select("id") { from("audit_log") }
        assertEquals("SELECT id FROM audit_log", query.sql)
        assertEquals(emptyList(), query.parameters)
    }

    @Test
    fun identifiersAreValidatedNotEscaped() {
        assertFailsWith<IllegalArgumentException> { select("id") { from("users; DROP TABLE users") } }
        assertFailsWith<IllegalArgumentException> { select("id, password") { from("users") } }
        assertFailsWith<IllegalArgumentException> { select("id") { from("users"); where { "name = '' OR 1=1 --" eq 1 } } }
        assertFailsWith<IllegalArgumentException> { select("id") { from("users"); orderBy("id DESC; --") } }
        assertFailsWith<IllegalArgumentException> { select("id") { from("users"); where { "role" isIn emptyList() } } }
    }

    @Test
    fun missingTableAndOversizedLimitFail() {
        assertFailsWith<IllegalStateException> { select("id") { } }
        assertFailsWith<IllegalArgumentException> { select("id") { from("users"); limit(1_000_000) } }
    }
}
