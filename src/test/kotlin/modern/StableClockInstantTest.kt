package atoz.modern

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant
import kotlin.time.toJavaInstant
import kotlin.time.toKotlinInstant

class StableClockInstantTest {
    private val start = Instant.parse("2026-04-23T09:00:00Z")

    @Test
    fun injectedClockMakesExpiryTestable() {
        val clock = SettableClock(start)
        val service = ResetLinkService(clock)
        val link = service.issue("t")
        assertEquals(Instant.parse("2026-04-23T09:15:00Z"), link.expiresAt)
        clock.current = start + 15.minutes - 1.seconds
        assertTrue(service.isValid(link))
        clock.current = start + 15.minutes
        assertFalse(service.isValid(link))
        clock.current = start - 1.seconds // clock moved backwards: not yet issued
        assertFalse(service.isValid(link))
    }

    @Test
    fun instantArithmeticAndIso8601() {
        assertEquals("2026-04-23T09:00:00Z", start.toString())
        assertEquals(15.minutes, (start + 15.minutes) - start)
        assertEquals(1_776_934_800L, start.epochSeconds)
        assertTrue(start < start + 1.seconds)
        assertFailsWith<IllegalArgumentException> { Instant.parse("23/04/2026 09:00") }
    }

    @Test
    fun convertsToAndFromJavaTime() {
        val javaInstant = start.toJavaInstant()
        assertEquals(java.time.Instant.parse("2026-04-23T09:00:00Z"), javaInstant)
        assertEquals(start, javaInstant.toKotlinInstant())
    }

    @Test
    fun systemClockTracksRealTime() {
        val before = java.time.Instant.now().toKotlinInstant()
        val now = Clock.System.now()
        assertTrue(now >= before && now - before < 5.seconds)
    }
}
