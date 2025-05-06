package atoz.dsl

import kotlin.test.Test
import kotlin.test.assertEquals

class HtmlBuilderTest {
    @Test
    fun nestedLambdasBuildATree() {
        val page = html {
            body {
                h1 { +"Title" }
                ul {
                    li { +"a" }
                    li { +"b" }
                }
            }
        }
        assertEquals("<html><body><h1>Title</h1><ul><li>a</li><li>b</li></ul></body></html>", page)
    }

    @Test
    fun textAndAttributesAreEscaped() {
        val page = html {
            body {
                p {
                    attr("title", "\" onmouseover=\"x")
                    +"<script>alert(1)</script>"
                }
            }
        }
        assertEquals(
            "<html><body><p title=\"&quot; onmouseover=&quot;x\">&lt;script&gt;alert(1)&lt;/script&gt;</p></body></html>",
            page,
        )
    }
}
