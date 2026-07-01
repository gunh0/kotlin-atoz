package atoz.modern

import kotlin.test.Test
import kotlin.test.assertEquals

class AllMetaTargetTest {
    private val type = SignupRequest::class.java

    @Test
    fun defaultTargetIsNowParameterAndField() {
        assertEquals(listOf("param", "field"), redactedOn(type, "password", 1))
    }

    @Test
    fun allMetaTargetAddsTheGetter() {
        assertEquals(listOf("param", "field", "getter"), redactedOn(type, "recoveryCode", 2))
    }

    @Test
    fun explicitTargetStillMeansExactlyThat() {
        assertEquals(listOf("param"), redactedOn(type, "pin", 3))
        assertEquals(emptyList(), redactedOn(type, "email", 0))
    }

    @Test
    fun fieldBasedToolsNowSeeTheAnnotation() {
        val dump = redactedDump(SignupRequest("a@example.com", "hunter2", "RC-7781", "4321"))
        assertEquals("email=a@example.com, password=****, pin=4321, recoveryCode=****", dump)
    }
}
