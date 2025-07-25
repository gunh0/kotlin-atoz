package atoz.interop

import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import java.time.ZoneOffset
import kotlin.time.toKotlinDuration

/*
java.time works from Kotlin as is, and reads better than in Java because
Kotlin operator conventions apply to Java methods with matching names:
- `date + Period.ofDays(30)` calls LocalDate.plus, `instant - duration` minus.
- Comparable types get `<`, `>` and ranges: `today in start..end`.
- Getters read as properties: `date.dayOfWeek`, `duration.seconds`.
Inject a java.time.Clock instead of calling `Instant.now()` directly, so tests
can pin the time. `toKotlinDuration()` bridges to kotlin.time.Duration
(the Kotlin-native Clock/Instant are not stable in these Kotlin versions).
*/

class CertificateCheck(private val clock: Clock) {
    fun expiresWithin(notAfter: Instant, window: Duration): Boolean {
        val now = clock.instant()
        return notAfter > now && notAfter - window <= now
    }

    fun isValidOn(notBefore: LocalDate, notAfter: LocalDate): Boolean = LocalDate.now(clock) in notBefore..notAfter
}

fun renewalDate(issued: LocalDate, validity: Period, renewEarly: Period): LocalDate = issued + validity - renewEarly

fun main() {
    val clock = Clock.fixed(Instant.parse("2025-07-25T09:00:00Z"), ZoneOffset.UTC)
    val check = CertificateCheck(clock)
    val notAfter = Instant.parse("2025-08-10T00:00:00Z")
    println("expires within 30 days: ${check.expiresWithin(notAfter, Duration.ofDays(30))}")
    println("valid today: ${check.isValidOn(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31))}")
    val renew = renewalDate(LocalDate.of(2025, 7, 25), Period.ofDays(90), Period.ofDays(30))
    println("renew on $renew, a ${renew.dayOfWeek}")
    println("Seoul now: ${clock.instant().atZone(ZoneId.of("Asia/Seoul"))}")
    println("as kotlin.time.Duration: ${Duration.ofMinutes(90).toKotlinDuration()}")
}
