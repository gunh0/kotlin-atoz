package atoz.dsl

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ValidationDslTest {
    @Test
    fun validInputHasNoViolations() {
        assertEquals(emptyList(), registrationRules.validate(Registration("alice_01", "alice@example.com", 30)))
    }

    @Test
    fun everyViolationIsReportedWithItsFieldName() {
        val violations = registrationRules.validate(Registration("Al", "not-an-email", 12))
        assertEquals(
            listOf(
                Violation("username", "length must be in 3..32"),
                Violation("username", "must be lowercase letters, digits or _"),
                Violation("email", "must be an email address"),
                Violation("age", "must be at least 16"),
            ),
            violations,
        )
    }

    @Test
    fun customRulesAndBlankInput() {
        val violations = registrationRules.validate(Registration(" ", "a@b.io", 200))
        assertTrue(Violation("username", "must not be blank") in violations)
        assertTrue(Violation("age", "must be a plausible age") in violations)
        assertTrue(violations.none { it.field == "email" })
    }

    @Test
    fun fieldNamesComeFromPropertyReferences() {
        data class Login(val user: String, val attempts: Int)
        val rules = validator<Login> {
            field(Login::user) { notBlank() }
            field(Login::attempts) { rule("too many attempts") { it < 5 } }
        }
        assertEquals(listOf(Violation("user", "must not be blank"), Violation("attempts", "too many attempts")), rules.validate(Login("", 9)))
    }
}
