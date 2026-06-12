package atoz.interop

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SealedFromJavaTest {
    @Test
    fun kotlinSealedTypesAreJvmSealedTypes() {
        assertTrue(AccessDecision::class.java.isSealed)
        assertTrue(AccessDecision::class.java.isInterface)
        assertEquals(listOf("Allow", "Challenge", "Deny"), permittedNames(AccessDecision::class.java))
        assertTrue(AuthMethod::class.java.isSealed)
        assertEquals(listOf("Federated", "Password"), permittedNames(AuthMethod::class.java))
    }

    @Test
    fun subclassesAreFinalUnlessOpen() {
        assertEquals("final", javaModifiers(AccessDecision.Allow::class.java))
        assertEquals("final", javaModifiers(AccessDecision.Challenge::class.java))
        assertEquals("final", javaModifiers(AuthMethod.Password::class.java))
        assertEquals("non-sealed", javaModifiers(AuthMethod.Federated::class.java))
    }

    @Test
    fun ordinaryClassesAreNotSealed() {
        assertFalse(String::class.java.isSealed)
        assertEquals(emptyList(), permittedNames(String::class.java))
    }
}
