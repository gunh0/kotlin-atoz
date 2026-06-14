package atoz.secure

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputFilter
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import java.util.concurrent.atomic.AtomicInteger

/*
Java deserialization instantiates whatever classes the byte stream names and
runs their readObject methods *before* your cast can reject the result. With a
"gadget" class on the classpath that is remote code execution, and even
without one, deep nesting or a huge array length is a denial of service.
Prefer a data format (JSON, protobuf) for untrusted input. If
ObjectInputStream is unavoidable, install an ObjectInputFilter (Java 9+): it
sees every class, array length, depth and reference count before anything is
created. From Kotlin it is a fun interface, so a lambda works; the JDK also
parses pattern strings ("maxdepth=5;com.example.*;!*") via
ObjectInputFilter.Config.createFilter, or JVM-wide via -Djdk.serialFilter.
*/

data class TransferRequest(val from: String, val to: String, val cents: Long) : Serializable

data class ChainLink(val next: ChainLink?) : Serializable

// Stands in for a gadget: something with side effects in readObject.
class SideEffectGadget : Serializable {
    private fun readObject(input: ObjectInputStream) {
        input.defaultReadObject()
        triggered.incrementAndGet()
    }

    companion object {
        val triggered = AtomicInteger()
    }
}

object SafeDeserialization {
    fun allowlist(
        allowed: Set<Class<*>>,
        maxDepth: Long = 10,
        maxArrayLength: Long = 10_000,
        maxReferences: Long = 1_000,
    ): ObjectInputFilter = ObjectInputFilter { info ->
        val type = info.serialClass()
        when {
            info.depth() > maxDepth || info.references() > maxReferences -> ObjectInputFilter.Status.REJECTED
            info.arrayLength() > maxArrayLength -> ObjectInputFilter.Status.REJECTED
            type == null -> ObjectInputFilter.Status.UNDECIDED // limits-only callback, nothing to check
            type.isArray && type.componentType.isPrimitive -> ObjectInputFilter.Status.ALLOWED
            type in allowed -> ObjectInputFilter.Status.ALLOWED
            else -> ObjectInputFilter.Status.REJECTED
        }
    }

    fun serialize(value: Serializable): ByteArray =
        ByteArrayOutputStream().also { bytes -> ObjectOutputStream(bytes).use { it.writeObject(value) } }.toByteArray()

    fun deserialize(bytes: ByteArray, filter: ObjectInputFilter?): Any? =
        ObjectInputStream(ByteArrayInputStream(bytes)).use { input ->
            if (filter != null) input.objectInputFilter = filter
            input.readObject()
        }
}

fun main() {
    val filter = SafeDeserialization.allowlist(setOf(TransferRequest::class.java))
    val transfer = SafeDeserialization.serialize(TransferRequest("alice", "bob", 1_000))
    println("allowed: ${SafeDeserialization.deserialize(transfer, filter)}")

    val gadget = SafeDeserialization.serialize(SideEffectGadget())
    val before = SideEffectGadget.triggered.get()
    SafeDeserialization.deserialize(gadget, filter = null)
    println("without filter, gadget readObject ran: ${SideEffectGadget.triggered.get() > before}")
    val afterUnfiltered = SideEffectGadget.triggered.get()
    val rejected = runCatching { SafeDeserialization.deserialize(gadget, filter) }.exceptionOrNull()
    println("with filter: $rejected; readObject ran: ${SideEffectGadget.triggered.get() > afterUnfiltered}")
}
