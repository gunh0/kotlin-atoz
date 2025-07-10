package atoz.oop

import kotlin.test.Test
import kotlin.test.assertEquals

class AbstractClassesTest {
    @Test
    fun templateMethodFillsInTheSteps() {
        val report = FindingsReport(listOf("a", "b"))
        assertEquals("# Findings\n- a\n- b\n-- 2 finding(s) --", report.render())
    }

    @Test
    fun abstractClassKeepsState() {
        val report = FindingsReport(emptyList())
        repeat(3) { report.render() }
        assertEquals(3, report.renderCount())
    }

    @Test
    fun subclassOverridesOnlyWhatIsOpen() {
        assertEquals("# Executive summary\n- x\n-- 1 finding(s) --", ExecutiveReport(listOf("x")).render())
        assertEquals("Findings", ExecutiveReport(listOf("x")).title)
    }
}
