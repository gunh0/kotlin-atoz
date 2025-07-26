package atoz.secure

/*
Injection happens when data is spliced into text that something else parses:
SQL, HTML, shell commands, regular expressions. Kotlin string templates make
the splicing *easier* to write, so they make the bug easier too:
`"... WHERE name = '$name'"` is the classic SQL injection.
The fix is the same as in Java: keep code and data separate.
- SQL: a fixed statement with `?` placeholders, values bound separately
  (JDBC PreparedStatement; simulated here so it runs without a database).
- HTML: escape on output (see the dsl HtmlBuilder) or use a template engine.
- Regex: `Regex.escape(input)` turns user text into a literal pattern.
- Processes: pass arguments as a list to ProcessBuilder, never through `sh -c`.
*/

fun naiveQuery(name: String): String = "SELECT * FROM users WHERE name = '$name'"

class BoundQuery(val sql: String, val parameters: List<Any?>) {
    init {
        require(sql.count { it == '?' } == parameters.size) { "expected ${sql.count { it == '?' }} parameters, got ${parameters.size}" }
        require('\'' !in sql) { "literals belong in parameters, not in the SQL text" }
    }
}

fun findUserQuery(name: String): BoundQuery = BoundQuery("SELECT * FROM users WHERE name = ?", listOf(name))

// A toy "database" that, like a real driver, compares bound values as data.
fun runBound(query: BoundQuery, rows: List<String>): List<String> = rows.filter { it == query.parameters.single() }

fun containsLiteral(text: String, userPattern: String): Boolean = Regex(Regex.escape(userPattern)).containsMatchIn(text)

fun grepCommand(userPattern: String, file: String): List<String> = listOf("grep", "-F", "--", userPattern, file)

fun main() {
    val input = "' OR '1'='1"
    println("naive: ${naiveQuery(input)}")
    val query = findUserQuery(input)
    println("bound: ${query.sql} with ${query.parameters}")
    println("rows: ${runBound(query, listOf("alice", "bob"))}")
    println("regex literal: ${containsLiteral("version 1x0", "1.0")}")
    println("process args: ${grepCommand("; rm -rf /", "app.log")}")
}
