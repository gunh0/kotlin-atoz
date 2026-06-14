package atoz.secure

import java.io.InvalidClassException
import java.io.ObjectInputFilter
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DeserializationFilterTest {
    private val transfers = SafeDeserialization.allowlist(setOf(TransferRequest::class.java))

    @Test
    fun allowlistedClassesStillWork() {
        val bytes = SafeDeserialization.serialize(TransferRequest("alice", "bob", 1_000))
        assertEquals(TransferRequest("alice", "bob", 1_000), SafeDeserialization.deserialize(bytes, transfers))
        val array = SafeDeserialization.serialize(byteArrayOf(1, 2, 3))
        assertContentEquals(byteArrayOf(1, 2, 3), SafeDeserialization.deserialize(array, transfers) as ByteArray)
    }

    @Test
    fun readObjectRunsBeforeAnyCastWithoutAFilter() {
        val bytes = SafeDeserialization.serialize(SideEffectGadget())
        val before = SideEffectGadget.triggered.get()
        val result = runCatching { SafeDeserialization.deserialize(bytes, filter = null) as TransferRequest }
        assertFailsWith<ClassCastException> { result.getOrThrow() }
        assertEquals(before + 1, SideEffectGadget.triggered.get()) // the cast came too late
    }

    @Test
    fun theFilterRejectsBeforeInstantiation() {
        val bytes = SafeDeserialization.serialize(SideEffectGadget())
        val before = SideEffectGadget.triggered.get()
        assertFailsWith<InvalidClassException> { SafeDeserialization.deserialize(bytes, transfers) }
        assertEquals(before, SideEffectGadget.triggered.get())
    }

    @Test
    fun depthAndArrayLimits() {
        val chains = SafeDeserialization.allowlist(setOf(ChainLink::class.java), maxDepth = 10)
        val shallow = (1..5).fold(null as ChainLink?) { next, _ -> ChainLink(next) }!!
        val deep = (1..50).fold(null as ChainLink?) { next, _ -> ChainLink(next) }!!
        assertEquals(shallow, SafeDeserialization.deserialize(SafeDeserialization.serialize(shallow), chains))
        assertFailsWith<InvalidClassException> { SafeDeserialization.deserialize(SafeDeserialization.serialize(deep), chains) }
        val big = SafeDeserialization.serialize(ByteArray(100_000))
        assertFailsWith<InvalidClassException> { SafeDeserialization.deserialize(big, transfers) }
    }

    @Test
    fun theJdkPatternSyntaxExpressesTheSameAllowlist() {
        val pattern = ObjectInputFilter.Config.createFilter("maxdepth=10;atoz.secure.TransferRequest;!*")
        val transfer = SafeDeserialization.serialize(TransferRequest("alice", "bob", 1))
        assertEquals(TransferRequest("alice", "bob", 1), SafeDeserialization.deserialize(transfer, pattern))
        val gadget = SafeDeserialization.serialize(SideEffectGadget())
        assertFailsWith<InvalidClassException> { SafeDeserialization.deserialize(gadget, pattern) }
    }
}
