package atoz.interop

/*
Java has no declaration-site variance, so Kotlin's `out`/`in` turn into
use-site wildcards in the JVM signatures Java callers (and reflection,
DI containers, mocking libraries) see:
- a parameter `List<Finding>` (List is `out E`) becomes `List<? extends Finding>`,
  so Java can pass a List<CriticalFinding>; `(Finding) -> Unit` (`in` parameter)
  becomes `Function1<? super Finding, Unit>`.
- return types get no wildcards (Java callers shouldn't have to deal with them),
  and neither do final type arguments: `List<String>` stays `List<String>`.
- @JvmSuppressWildcards removes them, e.g. when a framework matches exact
  generic types; @JvmWildcard forces one where it would be left out.
*/

open class Finding(val id: String)

class CriticalFinding(id: String) : Finding(id)

fun countFindings(findings: List<Finding>): Int = findings.size

fun countFindingsExact(findings: List<@JvmSuppressWildcards Finding>): Int = findings.size

fun forEachFinding(findings: List<Finding>, action: (Finding) -> Unit) = findings.forEach(action)

fun latestFindings(): List<Finding> = listOf(CriticalFinding("CVE-1"))

fun latestFindingsWildcard(): List<@JvmWildcard Finding> = latestFindings()

fun countIds(ids: List<String>): Int = ids.size

private val facade = Class.forName("atoz.interop.JvmWildcardsKt")

fun javaParameterTypes(name: String): List<String> =
    facade.methods.single { it.name == name }.genericParameterTypes.map { it.typeName }

fun javaReturnType(name: String): String = facade.methods.single { it.name == name }.genericReturnType.typeName

fun main() {
    for (name in listOf("countFindings", "countFindingsExact", "forEachFinding", "countIds")) {
        println("$name(${javaParameterTypes(name).joinToString()})")
    }
    for (name in listOf("latestFindings", "latestFindingsWildcard")) println("$name returns ${javaReturnType(name)}")
    println(countFindings(listOf(CriticalFinding("CVE-2")))) // List<CriticalFinding> is a List<Finding> in Kotlin anyway
}
