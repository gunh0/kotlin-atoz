package atoz.coroutines

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext

class ContextElementsTest {
    @Test
    fun elementsAreMissingUntilAdded() = runTest {
        assertEquals("[-] [-] boot", auditLine("boot"))
    }

    @Test
    fun childrenInheritAndWithContextOverrides() = runTest {
        withContext(CoroutineName("http") + RequestId("r-1")) {
            assertEquals("[http] [r-1] start", auditLine("start"))
            assertEquals("[worker] [r-1] child", async(CoroutineName("worker")) { auditLine("child") }.await())
            assertEquals("[http] [r-2] nested", withContext(RequestId("r-2")) { auditLine("nested") })
            assertEquals("[http] [r-1] end", auditLine("end"))
        }
    }

    @Test
    fun theContextIsAMapKeyedByElementType() {
        val context = CoroutineName("a") + RequestId("r-1") + RequestId("r-2")
        assertEquals("r-2", context[RequestId]?.value) // same key: the later element replaces the earlier one
        assertEquals("a", context[CoroutineName]?.name)
        assertSame(context[CoroutineName], context.minusKey(RequestId)[CoroutineName])
        assertNull(context.minusKey(RequestId)[RequestId])
    }

    @Test
    fun threadLocalsNeedAContextElementToFollowTheCoroutine() = runTest {
        currentTenant.set("acme")
        try {
            assertNull(tenantSeenOnDefault(propagate = false))
            assertEquals("acme", tenantSeenOnDefault(propagate = true))
        } finally {
            currentTenant.remove()
        }
    }
}
