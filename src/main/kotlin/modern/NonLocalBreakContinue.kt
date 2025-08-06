package atoz.modern

/*
Non-local `break` and `continue` (stable in 2.2): inside the lambda of an
inline function such as `forEach`, `let` or `use`, `break` and `continue` now
target the enclosing loop, just as `return` already returned from the
enclosing function. Before 2.2 both were compile errors there, which forced
labels, flags or rewriting the lambda as a nested `for`.
Note what they jump to: `continue` inside `forEach` moves on to the next
iteration of the *outer* loop, not to the next element of `forEach`.
This only works with inline lambdas, which are copied into the loop body;
a lambda that is stored or passed to a non-inline function still can't.
*/

// Counts words; "#..." skips the rest of the line, "__END__" stops reading altogether.
fun countWords(lines: List<String>): Int {
    var count = 0
    for (line in lines) {
        line.split(" ").filter { it.isNotEmpty() }.forEach { word ->
            if (word == "__END__") break // leaves the for loop
            if (word.startsWith("#")) continue // next line, not next word
            count++
        }
    }
    return count
}

// Header block parser: comments are skipped, a blank line ends the block.
fun parseHeaders(lines: List<String>): Map<String, String> {
    val headers = linkedMapOf<String, String>()
    for (line in lines) {
        line.trim().let { trimmed ->
            if (trimmed.isEmpty()) break
            if (trimmed.startsWith("#")) continue
            val parts = trimmed.split(":", limit = 2)
            if (parts.size != 2) continue
            headers[parts[0].trim().lowercase()] = parts[1].trim()
        }
    }
    return headers
}

fun main() {
    println(countWords(listOf("one two # three four", "five", "six __END__ seven", "eight")))
    println(parseHeaders(listOf("Host: example.com", "# comment", "garbage", "X-Token: abc", "", "body: not a header")))
}
