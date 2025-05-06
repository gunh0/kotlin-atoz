package atoz.dsl

/*
A type-safe builder: nested lambdas with receivers that mirror the structure
of the document. Each tag function creates a child element, runs the lambda
with that child as `this`, and adds it to the parent. The compiler only
allows the functions defined on the current element, so `li` exists inside
`ul` but not inside `p`, and the result is a tree, not a concatenated string.
Because text goes through `+"..."` (unaryPlus) and is escaped when rendered,
user input cannot inject markup, unlike building HTML with string templates.
*/

abstract class HtmlElement(private val name: String) {
    private val children = mutableListOf<HtmlElement>()
    private val attributes = linkedMapOf<String, String>()

    protected fun <T : HtmlElement> child(element: T, configure: T.() -> Unit): T {
        element.configure()
        children += element
        return element
    }

    fun attr(key: String, value: String) {
        attributes[key] = value
    }

    operator fun String.unaryPlus() {
        children += HtmlText(this)
    }

    open fun render(): String {
        val attrs = attributes.entries.joinToString("") { (k, v) -> " $k=\"${escapeHtml(v)}\"" }
        return "<$name$attrs>${children.joinToString("") { it.render() }}</$name>"
    }
}

class HtmlText(private val text: String) : HtmlElement("#text") {
    override fun render() = escapeHtml(text)
}

class HtmlPage : HtmlElement("html") {
    fun body(configure: HtmlBody.() -> Unit) = child(HtmlBody(), configure)
}

class HtmlBody : HtmlElement("body") {
    fun h1(configure: HtmlHeading.() -> Unit) = child(HtmlHeading(), configure)
    fun p(configure: HtmlParagraph.() -> Unit) = child(HtmlParagraph(), configure)
    fun ul(configure: HtmlList.() -> Unit) = child(HtmlList(), configure)
}

class HtmlHeading : HtmlElement("h1")
class HtmlParagraph : HtmlElement("p")

class HtmlList : HtmlElement("ul") {
    fun li(configure: HtmlListItem.() -> Unit) = child(HtmlListItem(), configure)
}

class HtmlListItem : HtmlElement("li")

fun escapeHtml(text: String): String = buildString {
    for (c in text) {
        when (c) {
            '<' -> append("&lt;")
            '>' -> append("&gt;")
            '&' -> append("&amp;")
            '"' -> append("&quot;")
            '\'' -> append("&#39;")
            else -> append(c)
        }
    }
}

fun html(configure: HtmlPage.() -> Unit): String = HtmlPage().apply(configure).render()

fun main() {
    val findings = listOf("SQL injection", "<script>alert(1)</script>")
    println(
        html {
            body {
                h1 { +"Scan report" }
                p {
                    attr("class", "summary")
                    +"${findings.size} findings"
                }
                ul { for (f in findings) li { +f } }
            }
        },
    )
}
