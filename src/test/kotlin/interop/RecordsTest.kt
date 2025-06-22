package atoz.interop

import jdk.net.UnixDomainPrincipal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RecordsTest {
    @Test
    fun jvmRecordCompilesToAJavaRecord() {
        val type = Coordinate::class.java
        assertTrue(type.isRecord)
        assertEquals("java.lang.Record", type.superclass.name)
        assertEquals(listOf("lat", "lon"), type.recordComponents.map { it.name })
        assertEquals(1.5, type.getMethod("lat").invoke(Coordinate(1.5, 2.0)))
        assertFailsWith<NoSuchMethodException> { type.getMethod("getLat") }
    }

    @Test
    fun plainDataClassIsNot() {
        val type = PlainCoordinate::class.java
        assertFalse(type.isRecord)
        assertEquals(1.5, type.getMethod("getLat").invoke(PlainCoordinate(1.5, 2.0)))
    }

    @Test
    fun javaRecordComponentsReadAsProperties() {
        assertTrue(UnixDomainPrincipal::class.java.isRecord)
        assertEquals("alice:staff", describePrincipal("alice", "staff"))
        assertEquals(Coordinate(1.0, 2.0), Coordinate(1.0, 2.0))
    }
}
