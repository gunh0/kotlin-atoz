package atoz.oop

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class InterfacesTest {
    @Test
    fun defaultPropertyGetter() {
        assertEquals("Token", CacheEntry("token", 60).displayName)
    }

    @Test
    fun conflictingDefaultsAreResolvedExplicitly() {
        assertEquals("Token: audited, expires in 60s", CacheEntry("token", 60).describe())
    }

    @Test
    fun defaultMethodsCanBeOverridden() {
        assertTrue(CacheEntry("t", 60).isExpired(60))
        assertFalse(CacheEntry("t", 60).isExpired(59))
        assertFalse(Permanent("config").isExpired(Long.MAX_VALUE))
    }
}
