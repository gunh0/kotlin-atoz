package atoz.functional

import kotlin.test.Test
import kotlin.test.assertEquals

class FunctionReferencesTest {
    @Test
    fun topLevelAndUnboundMemberReferences() {
        assertEquals(
            listOf("10.0.0.1", "172.20.1.1", "192.168.0.10"),
            privateOnly(listOf(" 10.0.0.1", "8.8.8.8 ", "172.20.1.1", "172.40.1.1", "192.168.0.10")),
        )
    }

    @Test
    fun boundReference() {
        assertEquals(listOf("read", "write"), allowedOnly(listOf("read", "write", "admin"), setOf("read", "write")))
    }

    @Test
    fun propertyAndConstructorReferences() {
        val services = servicesFrom(listOf("ssh" to 22, "https" to 443))
        assertEquals(listOf(Service("ssh", 22), Service("https", 443)), services)
        assertEquals(listOf(22, 443), portsOf(services))
    }

    @Test
    fun referencesKeepTheirName() {
        assertEquals("isPrivateIp", referenceName())
    }
}
