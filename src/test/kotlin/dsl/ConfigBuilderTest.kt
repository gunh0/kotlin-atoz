package atoz.dsl

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class ConfigBuilderTest {
    @Test
    fun defaultsApply() {
        val config = serverConfig { host = "localhost" }
        assertEquals(8080, config.port)
        assertNull(config.tls)
        assertEquals(emptyList(), config.allowedOrigins)
    }

    @Test
    fun nestedBlockConfiguresTls() {
        val config = serverConfig {
            host = "api.example.com"
            tls {
                certificatePath = "/etc/tls/api.pem"
                protocols = listOf("TLSv1.2", "TLSv1.3")
            }
        }
        assertEquals(listOf("TLSv1.2", "TLSv1.3"), config.tls?.protocols)
    }

    @Test
    fun buildRejectsInvalidSettings() {
        assertEquals("host is required", assertFailsWith<IllegalArgumentException> { serverConfig { } }.message)
        assertEquals(
            "port must be in 1..65535, was 0",
            assertFailsWith<IllegalArgumentException> { serverConfig { host = "h"; port = 0 } }.message,
        )
        assertFailsWith<IllegalArgumentException> {
            serverConfig {
                host = "h"
                tls { certificatePath = "c"; protocols = listOf("SSLv3") }
            }
        }
        assertFailsWith<IllegalArgumentException> { serverConfig { host = "h"; allowOrigin("*") } }
    }

    @Test
    fun resultIsAnImmutableSnapshot() {
        val builder = ServerConfig.Builder().apply { host = "h"; allowOrigin("https://a") }
        val config = builder.build()
        builder.allowOrigin("https://b")
        assertEquals(listOf("https://a"), config.allowedOrigins)
    }
}
