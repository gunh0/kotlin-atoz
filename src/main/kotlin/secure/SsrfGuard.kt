package atoz.secure

import java.net.Inet6Address
import java.net.InetAddress
import java.net.URI

/*
SSRF: the server fetches a URL the user supplied (webhooks, link previews,
image imports) and ends up talking to localhost, the internal network or the
cloud metadata service at 169.254.169.254.
String checks on the host don't work: "2130706433", "127.1", "[::ffff:7f00:1]"
and DNS names that resolve to 10.0.0.5 all reach internal addresses. So:
- allowlist scheme and port, refuse credentials in the URL;
- resolve the host and check *every* address it returns (also unwrapping IPv4
  inside IPv6), refusing if any is internal;
- then connect to exactly the checked address, so a second DNS answer
  (rebinding) can't swap it, and repeat the check on every redirect.
The resolver is injected: production passes InetAddress::getAllByName.
*/

fun interface HostResolver {
    fun resolve(host: String): List<InetAddress>
}

sealed interface UrlDecision {
    data class Allowed(val uri: URI, val addresses: List<InetAddress>) : UrlDecision
    data class Blocked(val reason: String) : UrlDecision
}

class SsrfGuard(
    private val resolver: HostResolver = HostResolver { InetAddress.getAllByName(it).toList() },
    private val allowedPorts: Set<Int> = setOf(80, 443),
) {
    fun check(raw: String): UrlDecision {
        val uri = runCatching { URI(raw) }.getOrNull() ?: return UrlDecision.Blocked("malformed url")
        val scheme = uri.scheme?.lowercase()
        if (scheme != "http" && scheme != "https") return UrlDecision.Blocked("scheme not allowed")
        if (uri.rawUserInfo != null) return UrlDecision.Blocked("credentials in url")
        val host = uri.host?.removeSurrounding("[", "]") ?: return UrlDecision.Blocked("missing host")
        val port = if (uri.port == -1) (if (scheme == "https") 443 else 80) else uri.port
        if (port !in allowedPorts) return UrlDecision.Blocked("port not allowed")
        val addresses = runCatching { resolver.resolve(host) }.getOrNull().orEmpty()
        if (addresses.isEmpty()) return UrlDecision.Blocked("host does not resolve")
        addresses.firstOrNull(::isInternalAddress)?.let { return UrlDecision.Blocked("internal address ${it.hostAddress}") }
        return UrlDecision.Allowed(uri, addresses)
    }

    companion object {
        fun isInternalAddress(address: InetAddress): Boolean {
            val ip = embeddedIpv4(address) ?: address
            val b = ip.address.map { it.toInt() and 0xff }
            return ip.isAnyLocalAddress || ip.isLoopbackAddress || ip.isLinkLocalAddress ||
                ip.isSiteLocalAddress || ip.isMulticastAddress ||
                (b.size == 4 && (b[0] == 0 || (b[0] == 100 && b[1] in 64..127))) || // "this network", carrier-grade NAT
                (b.size == 16 && (b[0] and 0xfe) == 0xfc) // IPv6 unique local fc00::/7
        }

        // ::a.b.c.d (IPv4-compatible) and 64:ff9b::a.b.c.d (NAT64) carry an IPv4 address in the last 4 bytes.
        // (::ffff:a.b.c.d is already returned as an Inet4Address by the JDK.)
        private fun embeddedIpv4(address: InetAddress): InetAddress? {
            if (address !is Inet6Address) return null
            val bytes = address.address
            val nat64 = byteArrayOf(0, 0x64, -1, -101) + ByteArray(8)
            val compatible = bytes.copyOfRange(0, 12).all { it == 0.toByte() }
            if (!compatible && !bytes.copyOfRange(0, 12).contentEquals(nat64)) return null
            return InetAddress.getByAddress(bytes.copyOfRange(12, 16))
        }
    }
}

fun main() {
    val dns = mapOf(
        "hooks.example.com" to listOf("93.184.215.14"),
        "internal.example.com" to listOf("10.0.0.5"),
        "rebind.example.com" to listOf("93.184.215.14", "127.0.0.1"),
    )
    // Only literal IPs are parsed here, so nothing touches the network.
    val guard = SsrfGuard(HostResolver { host -> dns[host].orEmpty().map(InetAddress::getByName) })
    for (url in listOf(
        "https://hooks.example.com/notify", "http://internal.example.com/", "https://rebind.example.com/",
        "file:///etc/passwd", "https://hooks.example.com:8080/", "https://user:pw@hooks.example.com/",
    )) println("$url -> ${guard.check(url)}")
    val literal = SsrfGuard()
    for (url in listOf("http://2130706433/", "http://169.254.169.254/latest/meta-data/", "http://[::ffff:7f00:1]/")) {
        println("$url -> ${literal.check(url)}")
    }
}
