package atoz.oop

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class PropertyDelegationTest {
    @Test
    fun observableRecordsChanges() {
        val log = mutableListOf<String>()
        val setting = AuditedSetting(log)
        setting.level = "DEBUG"
        setting.level = "WARN"
        assertEquals(listOf("level: INFO -> DEBUG", "level: DEBUG -> WARN"), log)
    }

    @Test
    fun vetoableRejectsInvalidValues() {
        val setting = AuditedSetting(mutableListOf())
        setting.maxConnections = 500
        assertEquals(10, setting.maxConnections)
        setting.maxConnections = 50
        assertEquals(50, setting.maxConnections)
    }

    @Test
    fun notNullThrowsUntilAssigned() {
        val setting = AuditedSetting(mutableListOf())
        assertFailsWith<IllegalStateException> { setting.port }
        setting.port = 8080
        assertEquals(8080, setting.port)
    }

    @Test
    fun mapBackedProperties() {
        val config = ServiceConfig(mapOf("name" to "api", "replicas" to 3))
        assertEquals("api", config.name)
        assertEquals(3, config.replicas)
        assertFailsWith<NoSuchElementException> { ServiceConfig(emptyMap()).name }
    }

    @Test
    fun customDelegate() {
        val form = SignupForm()
        form.email = "  ian@example.com  "
        assertEquals("ian@example.com", form.email)
    }
}
