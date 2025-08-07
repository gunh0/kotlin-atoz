package atoz.dsl

/*
A DSL that ends in a validated, immutable object: the builder is mutable and
lenient while the block runs, `build()` checks everything at once, and the
caller only ever sees the immutable result. Invalid configuration fails at
startup with a clear message instead of somewhere deep at runtime.
Compared with a Java builder (`new ServerConfig.Builder().host(...).build()`),
nested blocks group related settings, defaults live in one place, and the
private constructor of the result makes the builder the only way in.
*/

class TlsSettings(val certificatePath: String, val protocols: List<String>)

class ServerConfig private constructor(
    val host: String,
    val port: Int,
    val tls: TlsSettings?,
    val allowedOrigins: List<String>,
) {
    class Builder {
        var host: String? = null
        var port: Int = 8080
        private var tls: TlsSettings? = null
        private val origins = mutableListOf<String>()

        fun tls(configure: TlsBuilder.() -> Unit) {
            tls = TlsBuilder().apply(configure).build()
        }

        fun allowOrigin(origin: String) {
            origins += origin
        }

        fun build(): ServerConfig {
            val h = requireNotNull(host) { "host is required" }
            require(h.isNotBlank()) { "host must not be blank" }
            require(port in 1..65535) { "port must be in 1..65535, was $port" }
            require("*" !in origins) { "wildcard origin is not allowed, list origins explicitly" }
            return ServerConfig(h, port, tls, origins.toList())
        }
    }

    class TlsBuilder {
        var certificatePath: String? = null
        var protocols: List<String> = listOf("TLSv1.3")

        fun build(): TlsSettings {
            val cert = requireNotNull(certificatePath) { "tls.certificatePath is required" }
            require(protocols.isNotEmpty()) { "tls.protocols must not be empty" }
            require(protocols.all { it in setOf("TLSv1.2", "TLSv1.3") }) { "unsupported protocol in $protocols" }
            return TlsSettings(cert, protocols.toList())
        }
    }
}

fun serverConfig(configure: ServerConfig.Builder.() -> Unit): ServerConfig =
    ServerConfig.Builder().apply(configure).build()

fun main() {
    val config = serverConfig {
        host = "api.example.com"
        port = 8443
        tls { certificatePath = "/etc/tls/api.pem" }
        allowOrigin("https://app.example.com")
    }
    println("${config.host}:${config.port} tls=${config.tls?.protocols} origins=${config.allowedOrigins}")
    println(runCatching { serverConfig { host = "x"; port = 70_000 } }.exceptionOrNull()?.message)
}
