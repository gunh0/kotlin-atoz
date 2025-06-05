package atoz.secure

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SecretToStringTest {
    @Test
    fun dataClassToStringLeaksEveryProperty() {
        assertEquals("LeakyCredentials(user=alice, password=hunter2)", LeakyCredentials("alice", "hunter2").toString())
        val message = runCatching { error("login failed for ${LeakyCredentials("alice", "hunter2")}") }.exceptionOrNull()!!.message!!
        assertTrue("hunter2" in message)
    }

    @Test
    fun overriddenToStringRedacts() {
        val text = "${RedactedCredentials("alice", "hunter2")}"
        assertFalse("hunter2" in text)
        assertEquals("hunter2", RedactedCredentials("alice", "hunter2").component2()) // still reachable on purpose
    }

    @Test
    fun valueClassRedactsWhereverItIsEmbedded() {
        val config = ApiClientConfig("https://api.example.com", Secret("sk_live_abc123"))
        assertEquals("ApiClientConfig(endpoint=https://api.example.com, apiKey=Secret(****))", config.toString())
        assertEquals("[Secret(****)]", listOf(config.apiKey).toString())
        assertEquals("sk_live_abc123", config.apiKey.reveal())
    }

    @Test
    fun valueClassCompilesToTheUnderlyingType() {
        val getter = ApiClientConfig::class.java.methods.single { it.name.startsWith("getApiKey") }
        assertEquals(String::class.java, getter.returnType)
    }
}
