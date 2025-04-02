package atoz.oop

// A Kotlin property is a field plus accessors, and callers always go through
// the accessors (no getX()/setX() boilerplate, no public fields). Custom
// accessors refer to the backing field as `field`; a property whose getter
// never touches `field` has no storage at all (a computed property).
// `private set` keeps writes inside the class.
//
// `lateinit var` is for non-null properties assigned after construction
// (dependency injection, test setup): reading one too early throws
// UninitializedPropertyAccessException instead of a vague NPE. `by lazy`
// computes a `val` on first access and caches it (thread-safe by default).

class Thermostat(initial: Int) {
    var target: Int = initial.coerceIn(MIN, MAX)
        set(value) {
            field = value.coerceIn(MIN, MAX)
            changes++
        }

    var changes: Int = 0
        private set

    val isEco: Boolean
        get() = target <= 19

    companion object {
        const val MIN = 10
        const val MAX = 30
    }
}

class AuthSession {
    lateinit var token: String

    val isAuthenticated: Boolean
        get() = ::token.isInitialized
}

class LazyConfig(private val loader: () -> String) {
    var loads = 0
        private set

    val content: String by lazy {
        loads++
        loader()
    }
}

fun main() {
    val t = Thermostat(21)
    t.target = 45
    println("target clamped to ${t.target}, changes = ${t.changes}, eco = ${t.isEco}")
    t.target = 18
    println("target ${t.target}, changes = ${t.changes}, eco = ${t.isEco}")

    val session = AuthSession()
    println("authenticated: ${session.isAuthenticated}, early read -> ${runCatching { session.token }.exceptionOrNull()}")
    session.token = "abc123"
    println("authenticated: ${session.isAuthenticated}")

    val config = LazyConfig { "port=8080" }
    println("loads before access: ${config.loads}")
    repeat(3) { config.content }
    println("loads after 3 reads: ${config.loads}")
}
