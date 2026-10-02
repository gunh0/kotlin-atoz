package atoz.dsl

/*
A type-state builder: the compiler tracks which required steps are done.
`RequestDraft<U, M>` carries two phantom type parameters, never stored
anywhere, that are either `Missing` or `Present`. Each step is an extension
function that only accepts a draft in the right state and returns the next:
- `url()` exists only on `RequestDraft<Missing, M>`, so setting it twice
  doesn't compile;
- `build()` exists only on `RequestDraft<Present, Present>`, so forgetting
  the URL or the method is a compile error, not an IllegalStateException
  at runtime like a classic Java builder's `build()`.
Optional settings (`header`) keep the state unchanged. Every step returns a
new immutable draft, so a half-built draft can be reused safely.
*/

sealed interface Missing

sealed interface Present

class HttpRequestSpec internal constructor(val method: String, val url: String, val headers: Map<String, String>) {
    override fun toString() = "$method $url $headers"
}

class RequestDraft<U, M> private constructor(
    internal val url: String?,
    internal val method: String?,
    internal val headers: Map<String, String>,
) {
    internal fun <U2, M2> next(url: String? = this.url, method: String? = this.method, headers: Map<String, String> = this.headers) =
        RequestDraft<U2, M2>(url, method, headers)

    companion object {
        internal fun start(): RequestDraft<Missing, Missing> = RequestDraft(null, null, emptyMap())
    }
}

fun request(): RequestDraft<Missing, Missing> = RequestDraft.start()

fun <M> RequestDraft<Missing, M>.url(url: String): RequestDraft<Present, M> {
    require(url.startsWith("https://")) { "only https URLs are allowed" }
    return next(url = url)
}

fun <U> RequestDraft<U, Missing>.method(method: String): RequestDraft<U, Present> {
    require(method in setOf("GET", "POST", "PUT", "DELETE")) { "unsupported method $method" }
    return next(method = method)
}

fun <U, M> RequestDraft<U, M>.header(name: String, value: String): RequestDraft<U, M> {
    require(name.none { it == '\r' || it == '\n' } && value.none { it == '\r' || it == '\n' }) { "header must not contain CR/LF" }
    return next(headers = headers + (name to value))
}

fun RequestDraft<Present, Present>.build(): HttpRequestSpec = HttpRequestSpec(method!!, url!!, headers)

fun main() {
    val base = request().header("User-Agent", "atoz/1.0")
    val get = base.url("https://api.example.com/users").method("GET").build()
    val post = base.method("POST").url("https://api.example.com/users").header("Content-Type", "application/json").build()
    println(get)
    println(post)
    // request().method("GET").build()      // does not compile: url is Missing
    // request().url("https://a").url("https://b") // does not compile: url already Present
    println(runCatching { base.header("X-Injected", "a\r\nSet-Cookie: x") }.exceptionOrNull()?.message)
}
