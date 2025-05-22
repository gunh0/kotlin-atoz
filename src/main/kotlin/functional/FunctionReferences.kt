package atoz.functional

// `::name` turns an existing function, property or constructor into a function
// value, the counterpart of Java's method references (Integer::parseInt).
// - top-level function:  ::isPrivateIp            (String) -> Boolean
// - member, unbound:     String::trim             (String) -> String
// - member, bound:       allowed::contains        (String) -> Boolean
// - property:            Service::port            (Service) -> Int
// - constructor:         ::Service                (String, Int) -> Service
// A reference keeps its name (::isPrivateIp.name), and passing one is often
// clearer than a lambda that only forwards its argument.

data class Service(val name: String, val port: Int)

fun isPrivateIp(ip: String): Boolean =
    ip.startsWith("10.") || ip.startsWith("192.168.") ||
        ip.split('.').let { it.size == 4 && it[0] == "172" && it[1].toIntOrNull() in 16..31 }

fun privateOnly(ips: List<String>): List<String> = ips.map(String::trim).filter(::isPrivateIp)

fun allowedOnly(requested: List<String>, allowed: Set<String>): List<String> = requested.filter(allowed::contains)

fun portsOf(services: List<Service>): List<Int> = services.map(Service::port)

fun servicesFrom(pairs: List<Pair<String, Int>>): List<Service> {
    val make: (String, Int) -> Service = ::Service
    return pairs.map { (name, port) -> make(name, port) }
}

fun referenceName(): String = ::isPrivateIp.name

fun main() {
    println(privateOnly(listOf(" 10.0.0.1", "8.8.8.8 ", "172.20.1.1", "172.40.1.1", "192.168.0.10")))
    println(allowedOnly(listOf("read", "write", "admin"), setOf("read", "write")))
    val services = servicesFrom(listOf("ssh" to 22, "https" to 443))
    println("$services -> ports ${portsOf(services)}")
    println("reference name: ${referenceName()}")
}
