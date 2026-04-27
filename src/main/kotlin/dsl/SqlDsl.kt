package atoz.dsl

/*
A query DSL that cannot produce SQL injection: values passed to `eq`, `gt` or
`isIn` never become part of the SQL text. Each one turns into a `?`
placeholder and goes into a parameter list for a PreparedStatement, as in
Java's JDBC. Identifiers (table and column names) can't be bound as
parameters, so the builder only accepts names matching a strict pattern and
rejects anything else.
Compared with string concatenation (`"... WHERE email = '$email'"`), the
DSL reads almost like SQL and the safe form is the only one you can write.
`@DslMarker` stops `where { }` from reaching the outer `select` builder.
*/

@DslMarker
annotation class SqlMarker

class SqlQuery(val sql: String, val parameters: List<Any?>)

private val sqlIdentifier = Regex("[A-Za-z_][A-Za-z0-9_]{0,62}")

private fun identifier(name: String): String {
    require(sqlIdentifier.matches(name)) { "invalid SQL identifier: $name" }
    return name
}

@SqlMarker
class SqlConditions(private val separator: String) {
    internal val clauses = mutableListOf<String>()
    internal val parameters = mutableListOf<Any?>()

    private fun add(column: String, operator: String, value: Any?) {
        clauses += "${identifier(column)} $operator ?"
        parameters += value
    }

    infix fun String.eq(value: Any?) = add(this, "=", value)

    infix fun String.gt(value: Any?) = add(this, ">", value)

    infix fun String.isIn(values: Collection<Any?>) {
        require(values.isNotEmpty()) { "IN needs at least one value" }
        clauses += "${identifier(this)} IN (${values.joinToString(", ") { "?" }})"
        parameters.addAll(values)
    }

    fun anyOf(configure: SqlConditions.() -> Unit) {
        val group = SqlConditions(" OR ").apply(configure)
        if (group.clauses.isEmpty()) return
        clauses += "(${group.render()})"
        parameters.addAll(group.parameters)
    }

    internal fun render(): String = clauses.joinToString(separator)
}

@SqlMarker
class SelectBuilder(columns: List<String>) {
    private val columns = columns.map(::identifier)
    private var table: String? = null
    private val conditions = SqlConditions(" AND ")
    private var order: String? = null
    private var limit: Int? = null

    fun from(table: String) {
        this.table = identifier(table)
    }

    fun where(configure: SqlConditions.() -> Unit) {
        conditions.configure()
    }

    fun orderBy(column: String) {
        order = identifier(column)
    }

    fun limit(rows: Int) {
        require(rows in 1..10_000) { "limit must be in 1..10000" }
        limit = rows
    }

    fun build(): SqlQuery {
        val from = checkNotNull(table) { "from(table) is required" }
        val sql = buildString {
            append("SELECT ${columns.joinToString(", ")} FROM $from")
            if (conditions.clauses.isNotEmpty()) append(" WHERE ${conditions.render()}")
            order?.let { append(" ORDER BY $it") }
            limit?.let { append(" LIMIT ?") }
        }
        return SqlQuery(sql, conditions.parameters + listOfNotNull(limit))
    }
}

fun select(vararg columns: String, configure: SelectBuilder.() -> Unit): SqlQuery {
    require(columns.isNotEmpty()) { "select at least one column" }
    return SelectBuilder(columns.toList()).apply(configure).build()
}

fun main() {
    val email = "alice@example.com' OR '1'='1"
    val query = select("id", "email") {
        from("users")
        where {
            "email" eq email
            "failed_logins" gt 3
            anyOf {
                "role" eq "admin"
                "role" isIn listOf("auditor", "owner")
            }
        }
        orderBy("id")
        limit(50)
    }
    println(query.sql)
    println(query.parameters)
    println(runCatching { select("id") { from("users; DROP TABLE users") } }.exceptionOrNull()?.message)
}
