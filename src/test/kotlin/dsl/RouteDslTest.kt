package atoz.dsl

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RouteDslTest {
    private val api = routes {
        "/api" {
            "GET /health" handledBy { "ok" }
            "/users" {
                "GET /" handledBy { "all users" }
                "GET /{id}" handledBy { params -> "user ${params["id"]}" }
                "DELETE /{id}" handledBy { params -> "deleted ${params["id"]}" }
            }
        }
    }

    @Test
    fun nestedBlocksPrefixPaths() {
        assertEquals("ok", api["GET", "/api/health"])
        assertEquals("all users", api["GET", "/api/users"])
        assertEquals("user 42", api["GET", "/api/users/42"])
        assertEquals("deleted 7", api["DELETE", "/api/users/7"])
    }

    @Test
    fun unknownMethodOrPathIsNotRouted() {
        assertNull(api["POST", "/api/users"])
        assertNull(api["GET", "/api/users/42/extra"])
        assertTrue("GET /api/health" in api)
        assertFalse("PUT /api/health" in api)
    }
}
