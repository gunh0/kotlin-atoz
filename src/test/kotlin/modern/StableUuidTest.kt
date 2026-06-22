package atoz.modern

import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

class StableUuidTest {
    private val text = "550e8400-e29b-41d4-a716-446655440000"

    @Test
    fun randomIsVersion4AndUnique() {
        val ids = List(1_000) { RequestId.next() }
        assertEquals(1_000, ids.toSet().size)
        val java = ids.first().uuid.toJavaUuid()
        assertEquals(4, java.version())
        assertEquals(2, java.variant()) // IETF variant
    }

    @Test
    fun parseOrNullForUntrustedInput() {
        assertEquals(text, RequestId.fromHeader(" $text ")?.uuid.toString())
        assertNull(RequestId.fromHeader("550e8400' OR 1=1 --"))
        assertNull(RequestId.fromHeader(""))
        assertNull(RequestId.fromHeader(null))
        assertFailsWith<IllegalArgumentException> { Uuid.parse("not-a-uuid") }
    }

    @Test
    fun formats() {
        val uuid = Uuid.parse(text)
        assertEquals(text, uuid.toString())
        assertEquals("550e8400e29b41d4a716446655440000", uuid.toHexString())
        assertEquals(uuid, Uuid.parseHex("550e8400e29b41d4a716446655440000"))
        assertEquals("00000000-0000-0000-0000-000000000000", Uuid.NIL.toString())
        assertEquals("req-$text", RequestId(uuid).toString())
    }

    @Test
    fun convertsToAndFromJavaUuid() {
        val java = UUID.fromString(text)
        assertEquals(java, Uuid.parse(text).toJavaUuid())
        assertEquals(Uuid.parse(text), java.toKotlinUuid())
        val random = UUID.randomUUID()
        assertEquals(random.toString(), random.toKotlinUuid().toString())
    }
}
