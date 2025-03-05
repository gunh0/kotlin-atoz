package atoz.dsl

import kotlin.test.Test
import kotlin.test.assertEquals

class LambdasWithReceiverTest {
    @Test
    fun builderLambdaRunsWithBuilderAsThis() {
        val text = email {
            to("alice@example.com")
            subject = "Hi"
            line("first")
            line("second")
        }
        assertEquals("To: alice@example.com\nSubject: Hi\n\nfirst\nsecond", text)
    }

    @Test
    fun receiverIsTheFirstParameter() {
        assertEquals(2, "kotlin".vowelCount())
        assertEquals(2, vowelCount("kotlin"))
        val plain: (String) -> Int = vowelCount // the same function type underneath
        assertEquals(5, plain("education"))
    }
}
