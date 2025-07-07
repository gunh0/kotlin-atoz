package atoz.dsl

/*
Operator conventions make a DSL read like configuration:
- `operator fun String.invoke(block)` lets a string literal open a nested
  path block: `"/api" { ... }`.
- `infix` functions drop the dot and parentheses: `GET handledBy { ... }`.
- `operator fun get(method, path)` gives a two-argument index lookup, and
  `operator fun contains` makes `"GET /x" in routes` work.
Extensions declared inside the scope class (like String.invoke here) only
exist inside the DSL block, so they don't pollute String everywhere else.
Java needs a fluent builder with explicit method chains for the same thing.
*/

typealias RouteHandler = (params: Map<String, String>) -> String

class RouteTable(private val routes: Map<String, RouteHandler>) {
    operator fun get(method: String, path: String): String? {
        for ((key, handler) in routes) {
            val (m, pattern) = key.split(" ", limit = 2)
            if (m != method) continue
            val params = matchPath(pattern, path) ?: continue
            return handler(params)
        }
        return null
    }

    operator fun contains(request: String): Boolean {
        val (method, path) = request.split(" ", limit = 2)
        return this[method, path] != null
    }

    private fun matchPath(pattern: String, path: String): Map<String, String>? {
        val expected = pattern.split("/")
        val actual = path.split("/")
        if (expected.size != actual.size) return null
        val params = mutableMapOf<String, String>()
        for ((e, a) in expected.zip(actual)) {
            when {
                e.startsWith("{") -> params[e.trim('{', '}')] = a
                e != a -> return null
            }
        }
        return params
    }
}

class RouteScope(private val prefix: String, private val routes: MutableMap<String, RouteHandler>) {
    operator fun String.invoke(block: RouteScope.() -> Unit) {
        RouteScope(prefix + this, routes).block()
    }

    infix fun String.handledBy(handler: RouteHandler) {
        val (method, path) = split(" ", limit = 2)
        routes["$method $prefix$path".trimEnd('/')] = handler
    }
}

fun routes(block: RouteScope.() -> Unit): RouteTable {
    val collected = linkedMapOf<String, RouteHandler>()
    RouteScope("", collected).block()
    return RouteTable(collected)
}

fun main() {
    val api = routes {
        "/api" {
            "GET /health" handledBy { "ok" }
            "/users" {
                "GET /" handledBy { "all users" }
                "GET /{id}" handledBy { params -> "user ${params["id"]}" }
                "DELETE /{id}" handledBy { params -> "deleted ${params["id"]}" }
            }
        }
    }
    println(api["GET", "/api/users/42"])
    println(api["GET", "/api/users"])
    println("POST /api/users" in api)
}
