package atoz.modern

import kotlin.test.Test
import kotlin.test.assertEquals

class MultiDollarInterpolationTest {
    @Test
    fun singleDollarIsLiteral() {
        val lines = jsonSchema("AuditEvent").lines()
        assertEquals("\"\$schema\": \"https://json-schema.org/draft/2020-12/schema\",", lines[1].trim())
        assertEquals(schemaLineOldStyle(), lines[1].trim())
        assertEquals("\"\$id\": \"https://example.com/schemas/auditevent.json\",", lines[2].trim())
        assertEquals("\"title\": \"AuditEvent\",", lines[3].trim())
    }

    @Test
    fun worksInEscapedStringsToo() {
        assertEquals("query(\$id: ID!) { user(id: \$id) { email } }", graphqlQuery("email"))
    }
}
