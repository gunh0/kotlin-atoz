package atoz.modern

/*
This chapter collects language and library changes by the Kotlin release that
made them stable, starting with what 2.2.0 promoted from preview.

Guard conditions in `when` (stable in 2.2): a branch of a `when` with a subject
can add `if <condition>` after its pattern, e.g. `is LoginFailed if x > 5 ->`.
Before, you had to drop the subject and repeat `e is LoginFailed && ...` in
every branch, or nest a second `when`. The pattern's smart cast applies inside
the guard, `else if` works as a guarded fallback, and exhaustiveness is still
checked: a guarded branch doesn't cover its type, so an unguarded one must follow.
Java 21's pattern matching has the same idea: `case LoginFailed f when f.attempts() > 5 ->`.
*/

sealed interface SecurityEvent {
    data class LoginFailed(val user: String, val attempts: Int) : SecurityEvent
    data class FileWrite(val path: String, val byAdmin: Boolean) : SecurityEvent
    data class Download(val bytes: Long) : SecurityEvent
    data object Logout : SecurityEvent
}

fun triage(event: SecurityEvent): String = when (event) {
    is SecurityEvent.LoginFailed if event.attempts >= 5 -> "alert: brute force on ${event.user}"
    is SecurityEvent.LoginFailed -> "info: failed login for ${event.user}"
    is SecurityEvent.FileWrite if event.path.startsWith("/etc/") && !event.byAdmin -> "alert: ${event.path} changed"
    is SecurityEvent.FileWrite -> "info: wrote ${event.path}"
    SecurityEvent.Logout -> "info: logout"
    else if event is SecurityEvent.Download && event.bytes > 1_000_000_000 -> "warn: large download"
    else -> "info: ${event::class.simpleName}"
}

fun main() {
    listOf(
        SecurityEvent.LoginFailed("alice", 1),
        SecurityEvent.LoginFailed("bob", 7),
        SecurityEvent.FileWrite("/etc/sudoers", byAdmin = false),
        SecurityEvent.FileWrite("/tmp/x", byAdmin = false),
        SecurityEvent.Download(5_000_000_000),
        SecurityEvent.Download(10),
        SecurityEvent.Logout,
    ).forEach { println(triage(it)) }
}
