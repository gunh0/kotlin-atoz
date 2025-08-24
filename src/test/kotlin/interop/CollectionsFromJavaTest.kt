package atoz.interop

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class CollectionsFromJavaTest {
    @Test
    fun readOnlyViewIsStillWritable() {
        val acl = AccessList()
        assertTrue(javaStyleAdd(acl.liveView, "mallory").isSuccess)
        assertTrue(acl.contains("mallory"))
    }

    @Test
    fun copyProtectsTheOriginal() {
        val acl = AccessList()
        val copy = acl.copy
        assertTrue(javaStyleAdd(copy, "mallory").isSuccess) // the copy itself is mutable
        assertFalse(acl.contains("mallory"))
    }

    @Test
    fun unmodifiableWrapperRejectsWrites() {
        val result = javaStyleAdd(AccessList().unmodifiableCopy, "mallory")
        assertIs<UnsupportedOperationException>(result.exceptionOrNull())
        assertIs<UnsupportedOperationException>(javaStyleAdd(java.util.List.copyOf(listOf("a")), "b").exceptionOrNull())
    }

    @Test
    fun listOfAllowsSetButNotAdd() {
            val list = listOf("a", "b") as MutableList<String>
        list[0] = "z"
        assertEquals(listOf("z", "b"), list)
        assertIs<UnsupportedOperationException>(javaStyleAdd(list, "c").exceptionOrNull())
    }
}
