package atoz.functional

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ScopeFunctionsTest {
    @Test
    fun applyReturnsTheConfiguredObject() {
        val config = secureConfig("api.example.com")
        assertEquals("https://api.example.com:443", config.url())
    }

    @Test
    fun alsoReturnsTheSameObjectAfterASideEffect() {
        val log = mutableListOf<String>()
        val config = createAndLog(log, "a.example")
        assertEquals(listOf("created https://a.example:443"), log)
        assertSame(config, config.also { })
    }

    @Test
    fun letRunAndWithReturnTheBlockResult() {
        assertEquals(4, portLength(" 8080 "))
        assertEquals(0, portLength(null))
        assertEquals("localhost:80 tls=false", describeConfig(ScopeServerConfig()))
        assertTrue(isDefaultPort(ScopeServerConfig()))
        assertFalse(isDefaultPort(ScopeServerConfig().apply { port = 8080 }))
    }
}
