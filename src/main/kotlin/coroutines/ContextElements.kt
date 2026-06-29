package atoz.coroutines

import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asContextElement
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/*
A coroutine context is a small map of elements keyed by type; Job and the
dispatcher are just two of them. You can add your own:
- CoroutineName("login") labels a coroutine for logs and debugging.
- A custom element (AbstractCoroutineContextElement plus a companion Key)
  carries request-scoped data such as a request id or the caller's principal.
  Children inherit the parent's elements; `withContext(RequestId("b"))`
  replaces one for that block only. Read it with `currentCoroutineContext()[Key]`.
- ThreadLocals (SLF4J MDC, security context holders) stay on the thread, but a
  coroutine moves between threads. `threadLocal.asContextElement(value)` sets
  the value on whichever thread the coroutine resumes and restores the old one
  afterwards; in Java you'd copy it into every Runnable by hand.
*/

class RequestId(val value: String) : AbstractCoroutineContextElement(RequestId) {
    companion object Key : CoroutineContext.Key<RequestId>

    override fun toString() = "RequestId($value)"
}

suspend fun auditLine(action: String): String {
    val context = currentCoroutineContext()
    val name = context[CoroutineName]?.name ?: "-"
    val requestId = context[RequestId]?.value ?: "-"
    return "[$name] [$requestId] $action"
}

val currentTenant = ThreadLocal<String?>()

suspend fun tenantSeenOnDefault(propagate: Boolean): String? {
    val context = if (propagate) Dispatchers.Default + currentTenant.asContextElement(currentTenant.get()) else Dispatchers.Default
    return withContext(context) { currentTenant.get() }
}

fun main() = runBlocking(CoroutineName("http") + RequestId("req-42")) {
    println(auditLine("request received"))
    launch(CoroutineName("audit-writer")) { println(auditLine("child inherits the request id")) }.join()
    withContext(RequestId("req-43")) { println(auditLine("overridden for this block")) }
    println(auditLine("back to the original"))

    currentTenant.set("acme")
    println("tenant on Default without element: ${tenantSeenOnDefault(propagate = false)}")
    println("tenant on Default with asContextElement: ${tenantSeenOnDefault(propagate = true)}")
    currentTenant.remove()
}
