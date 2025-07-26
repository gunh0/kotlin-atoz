package atoz.secure

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class InjectionSafetyTest {
    private val attack = "' OR '1'='1"

    @Test
    fun templatesSpliceInputIntoCode() {
        assertEquals("SELECT * FROM users WHERE name = '' OR '1'='1'", naiveQuery(attack))
    }

    @Test
    fun boundQueriesKeepInputAsData() {
        val query = findUserQuery(attack)
        assertEquals("SELECT * FROM users WHERE name = ?", query.sql) // same text for every input
        assertEquals(listOf<Any?>(attack), query.parameters)
        assertEquals(emptyList(), runBound(query, listOf("alice", "bob")))
        assertEquals(listOf("alice"), runBound(findUserQuery("alice"), listOf("alice", "bob")))
    }

    @Test
    fun boundQueryRejectsMisuse() {
        assertFailsWith<IllegalArgumentException> { BoundQuery("SELECT * FROM t WHERE a = ? AND b = ?", listOf(1)) }
        assertFailsWith<IllegalArgumentException> { BoundQuery("SELECT * FROM t WHERE a = '$attack'", emptyList()) }
    }

    @Test
    fun regexInputIsEscaped() {
        assertTrue(Regex("1.0").containsMatchIn("version 1x0")) // '.' matches anything
        assertFalse(containsLiteral("version 1x0", "1.0"))
        assertTrue(containsLiteral("version 1.0", "1.0"))
        assertFalse(containsLiteral("aaaa", "a+"))
    }

    @Test
    fun processArgumentsStaySeparate() {
        assertEquals(listOf("grep", "-F", "--", "; rm -rf /", "app.log"), grepCommand("; rm -rf /", "app.log"))
    }
}
