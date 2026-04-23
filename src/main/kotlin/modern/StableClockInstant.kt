package atoz.modern

import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant
import kotlin.time.toJavaInstant

/*
`kotlin.time.Instant` and `kotlin.time.Clock` (stable in 2.3): a point on the
UTC time line and a source of "now", in the standard library itself. Before
2.3 they needed @ExperimentalTime (or kotlinx-datetime), so JVM code used
java.time.Instant and java.time.Clock instead.
- Instant ± Duration, `<`/`>` comparisons, ISO-8601 `parse`/`toString`,
  `epochSeconds`; `toJavaInstant()`/`toKotlinInstant()` at the JVM boundary.
- Clock has one function, `now()`. Clock.System is the real one.
As with java.time.Clock, inject the clock instead of calling
`Clock.System.now()` inside logic: expiry rules become testable without sleeping.
*/

class ResetLink(val token: String, val issuedAt: Instant, val ttl: Duration = 15.minutes) {
    val expiresAt: Instant get() = issuedAt + ttl

    // Valid strictly before expiresAt: at the exact expiry instant the link is dead.
    fun isValidAt(now: Instant): Boolean = now >= issuedAt && now < expiresAt
}

class ResetLinkService(private val clock: Clock = Clock.System) {
    fun issue(token: String): ResetLink = ResetLink(token, clock.now())

    fun isValid(link: ResetLink): Boolean = link.isValidAt(clock.now())
}

// A controllable clock for tests and demos.
class SettableClock(var current: Instant) : Clock {
    override fun now(): Instant = current
}

fun main() {
    val clock = SettableClock(Instant.parse("2026-04-23T09:00:00Z"))
    val service = ResetLinkService(clock)
    val link = service.issue("r_8f2k")
    println("issued ${link.issuedAt}, expires ${link.expiresAt}")
    clock.current += 14.minutes
    println("after 14 min valid: ${service.isValid(link)}")
    clock.current += 1.minutes
    println("after 15 min valid: ${service.isValid(link)}")
    println("as java.time.Instant: ${link.expiresAt.toJavaInstant()} (epoch ${link.expiresAt.epochSeconds})")
    println("system clock: ${Clock.System.now() > link.expiresAt}")
}
