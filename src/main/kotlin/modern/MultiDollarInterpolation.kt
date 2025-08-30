package atoz.modern

/*
Multi-dollar string interpolation (stable in 2.2): prefix a string literal
with `$$` and only `$$name` or `$${expr}` interpolate; a single `$` is plain
text. With `$$$` you'd need three, and so on.
This helps with text that is full of literal dollar signs: JSON Schema
(`$schema`, `$ref`), GraphQL variables, shell snippets, templates meant for
another template engine. Before, each literal `$` in a raw string had to be
written as `${'$'}`, because raw strings have no backslash escapes.
It works for both "..." and """...""" strings.
*/

fun jsonSchema(typeName: String): String = $$"""
    {
      "$schema": "https://json-schema.org/draft/2020-12/schema",
      "$id": "https://example.com/schemas/$${typeName.lowercase()}.json",
      "title": "$$typeName",
      "type": "object"
    }
""".trimIndent()

// The same first line before 2.2.
fun schemaLineOldStyle(): String = """"${'$'}schema": "https://json-schema.org/draft/2020-12/schema","""

fun graphqlQuery(field: String): String = $$"query($id: ID!) { user(id: $id) { $$field } }"

fun main() {
    println(jsonSchema("AuditEvent"))
    println(graphqlQuery("email"))
}
