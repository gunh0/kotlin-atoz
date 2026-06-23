package atoz.interop

import kotlin.test.Test
import kotlin.test.assertEquals

class JvmWildcardsTest {
    @Test
    fun parametersGetWildcardsFromDeclarationSiteVariance() {
        assertEquals(listOf("java.util.List<? extends atoz.interop.Finding>"), javaParameterTypes("countFindings"))
        assertEquals(
            listOf(
                "java.util.List<? extends atoz.interop.Finding>",
                "kotlin.jvm.functions.Function1<? super atoz.interop.Finding, kotlin.Unit>",
            ),
            javaParameterTypes("forEachFinding"),
        )
    }

    @Test
    fun finalTypeArgumentsAndReturnTypesStayExact() {
        assertEquals(listOf("java.util.List<java.lang.String>"), javaParameterTypes("countIds"))
        assertEquals("java.util.List<atoz.interop.Finding>", javaReturnType("latestFindings"))
    }

    @Test
    fun annotationsOverrideTheDefaults() {
        assertEquals(listOf("java.util.List<atoz.interop.Finding>"), javaParameterTypes("countFindingsExact"))
        assertEquals("java.util.List<? extends atoz.interop.Finding>", javaReturnType("latestFindingsWildcard"))
    }

    @Test
    fun kotlinCallersAreUnaffected() {
        val critical: List<CriticalFinding> = listOf(CriticalFinding("a"), CriticalFinding("b"))
        assertEquals(2, countFindings(critical))
        assertEquals(2, countFindingsExact(critical)) // covariance still applies on the Kotlin side
    }
}
