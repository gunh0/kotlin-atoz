package atoz.secure

import java.net.InetAddress
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class SsrfGuardTest {
    // Literal IPs only: InetAddress.getByName parses them without a DNS lookup.
    private fun ip(literal: String): InetAddress = InetAddress.getByName(literal)

    private val dns = mapOf(
        "hooks.example.com" to listOf(ip("93.184.215.14")),
        "internal.example.com" to listOf(ip("10.0.0.5")),
        "rebind.example.com" to listOf(ip("93.184.215.14"), ip("127.0.0.1")),
        "v6.example.com" to listOf(ip("2606:2800:21f:cb07:6820:80da:af6b:8b2c")),
    )
    private val guard = SsrfGuard(HostResolver { host -> dns[host] ?: ip(host).let(::listOf) })

    private fun blockedReason(url: String) = assertIs<UrlDecision.Blocked>(guard.check(url), url).reason

    @Test
    fun publicHostsAreAllowedWithTheAddressesToPin() {
        val decision = assertIs<UrlDecision.Allowed>(guard.check("https://hooks.example.com/notify?id=1"))
        assertEquals(listOf(ip("93.184.215.14")), decision.addresses)
        assertIs<UrlDecision.Allowed>(guard.check("http://v6.example.com/"))
    }

    @Test
    fun schemePortAndCredentialsAreAllowlisted() {
        assertEquals("scheme not allowed", blockedReason("file:///etc/passwd"))
        assertEquals("scheme not allowed", blockedReason("gopher://hooks.example.com/"))
        assertEquals("port not allowed", blockedReason("https://hooks.example.com:8080/"))
        assertEquals("credentials in url", blockedReason("https://user:pw@hooks.example.com/"))
        assertEquals("malformed url", blockedReason("http://exa mple.com/"))
    }

    @Test
    fun everyResolvedAddressIsChecked() {
        assertEquals("internal address 10.0.0.5", blockedReason("https://internal.example.com/"))
        assertEquals("internal address 127.0.0.1", blockedReason("https://rebind.example.com/"))
    }

    @Test
    fun obfuscatedLiteralsResolveToInternalAddresses() {
        assertEquals(ip("127.0.0.1"), ip("2130706433")) // the JDK accepts a single 32-bit number
        assertEquals("internal address 127.0.0.1", blockedReason("http://2130706433/"))
        assertEquals("internal address 127.0.0.1", blockedReason("http://[::ffff:7f00:1]/"))
        assertEquals("internal address 169.254.169.254", blockedReason("http://169.254.169.254/latest/meta-data/"))
        assertTrue(blockedReason("http://[::1]/").startsWith("internal address"))
    }

    @Test
    fun internalRanges() {
        val internal = listOf(
            "0.0.0.0", "127.8.8.8", "10.1.2.3", "172.16.0.1", "192.168.1.1", "169.254.169.254", "100.64.0.1",
            "224.0.0.1", "::", "::1", "fe80::1", "fd00::1", "::127.0.0.1", "64:ff9b::a00:5",
        )
        for (address in internal) assertTrue(SsrfGuard.isInternalAddress(ip(address)), address)
        for (address in listOf("93.184.215.14", "8.8.8.8", "172.32.0.1", "100.128.0.1", "2606:4700::1111", "64:ff9b::808:808")) {
            assertFalse(SsrfGuard.isInternalAddress(ip(address)), address)
        }
    }
}
