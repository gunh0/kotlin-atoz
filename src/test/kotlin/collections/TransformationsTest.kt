package atoz.collections

import kotlin.test.Test
import kotlin.test.assertEquals

class TransformationsTest {
    private val logs = parseAccessLog(
        listOf(
            "10.0.0.5 /admin/login 401",
            "10.0.0.5 /admin/login 401",
            "garbage line",
            "10.0.0.9 /api/users 200",
            "10.0.0.7 /admin/config 403",
            "10.0.0.8 /x notanumber",
        ),
    )

    @Test
    fun mapNotNullDropsUnparsableLines() {
        assertEquals(4, logs.size)
        assertEquals(AccessLog("10.0.0.9", "/api/users", 200), logs[2])
    }

    @Test
    fun filterMapDistinct() {
        assertEquals(listOf("10.0.0.5", "10.0.0.7"), suspiciousIps(logs))
    }

    @Test
    fun flatMapFlattensOneToMany() {
        assertEquals(listOf("admin", "login", "api", "users", "config"), pathSegments(logs))
    }

    @Test
    fun filterIsInstanceAndTakeWhile() {
        assertEquals(listOf(1, 4), numbersOnly(listOf(1, "two", 3.0, 4)))
        assertEquals(listOf("Host: a"), headerUntilBlank(listOf("Host: a", "", "body")))
    }
}
