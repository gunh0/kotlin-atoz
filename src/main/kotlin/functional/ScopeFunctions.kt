package atoz.functional

// Five scope functions run a block on an object; they differ in two ways:
// how the block sees the object (`this` receiver or `it` argument) and what
// the call returns (the object itself or the block's result).
//
//            object as   returns          typical use
//   let      it          block result     null checks, transforming a value
//   run      this        block result     compute something from an object
//   with     this        block result     run(...) for a non-null argument
//   apply    this        the object       configure a new object
//   also     it          the object       side effects: logging, validation

class ScopeServerConfig {
    var host: String = "localhost"
    var port: Int = 80
    var tls: Boolean = false
    fun url(): String = (if (tls) "https" else "http") + "://$host:$port"
}

fun secureConfig(host: String): ScopeServerConfig = ScopeServerConfig().apply {
    this.host = host
    port = 443
    tls = true
}

fun portLength(raw: String?): Int = raw?.let { it.trim().length } ?: 0

fun describeConfig(config: ScopeServerConfig): String = with(config) { "$host:$port tls=$tls" }

fun isDefaultPort(config: ScopeServerConfig): Boolean = config.run { (tls && port == 443) || (!tls && port == 80) }

fun createAndLog(log: MutableList<String>, host: String): ScopeServerConfig =
    secureConfig(host).also { log += "created ${it.url()}" }

fun main() {
    val log = mutableListOf<String>()
    val config = createAndLog(log, "api.example.com")
    println(log)
    println(describeConfig(config))
    println("default port: ${isDefaultPort(config)}")
    println("portLength(\" 8080 \") = ${portLength(" 8080 ")}, portLength(null) = ${portLength(null)}")
}
