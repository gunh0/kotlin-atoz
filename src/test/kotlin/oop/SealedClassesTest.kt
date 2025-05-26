package atoz.oop

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SealedClassesTest {
    @Test
    fun exhaustiveWhenCoversEverySubtype() {
        assertEquals("loading...", renderFetch(FetchResult.Loading))
        assertEquals("200 OK (2 bytes)", renderFetch(FetchResult.Success("{}")))
        assertEquals("error 404: not found", renderFetch(FetchResult.Failure(404, "not found")))
    }

    @Test
    fun subtypesCarryTheirOwnData() {
        assertTrue(isRetryable(FetchResult.Failure(503, "unavailable")))
        assertFalse(isRetryable(FetchResult.Failure(400, "bad request")))
        assertFalse(isRetryable(FetchResult.Success("")))
    }

    @Test
    fun dataObjectHasReadableToString() {
        assertEquals("Loading", FetchResult.Loading.toString())
        assertEquals("Failure(code=500, reason=x)", FetchResult.Failure(500, "x").toString())
    }
}
