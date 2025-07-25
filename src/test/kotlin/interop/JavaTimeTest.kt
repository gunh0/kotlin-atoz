package atoz.interop

import java.time.Clock
import java.time.DayOfWeek
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneOffset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toKotlinDuration

class JavaTimeTest {
    private val clock = Clock.fixed(Instant.parse("2025-07-25T09:00:00Z"), ZoneOffset.UTC)
    private val check = CertificateCheck(clock)

    @Test
    fun fixedClockMakesTimeChecksDeterministic() {
        assertTrue(check.expiresWithin(Instant.parse("2025-08-10T00:00:00Z"), Duration.ofDays(30)))
        assertFalse(check.expiresWithin(Instant.parse("2025-12-01T00:00:00Z"), Duration.ofDays(30)))
        assertFalse(check.expiresWithin(Instant.parse("2025-07-01T00:00:00Z"), Duration.ofDays(30))) // already expired
        assertTrue(check.isValidOn(LocalDate.of(2025, 7, 25), LocalDate.of(2025, 7, 25)))
        assertFalse(check.isValidOn(LocalDate.of(2025, 7, 26), LocalDate.of(2025, 12, 31)))
    }

    @Test
    fun operatorsWorkOnJavaTypes() {
        val renew = renewalDate(LocalDate.of(2025, 7, 25), Period.ofDays(90), Period.ofDays(30))
        assertEquals(LocalDate.of(2025, 9, 23), renew)
        assertEquals(DayOfWeek.TUESDAY, renew.dayOfWeek)
        assertTrue(LocalDate.of(2025, 1, 1) < renew)
        assertEquals(90.minutes, Duration.ofMinutes(90).toKotlinDuration())
    }
}
