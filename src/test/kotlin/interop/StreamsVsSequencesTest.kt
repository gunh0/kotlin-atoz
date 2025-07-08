package atoz.interop

import java.util.stream.Stream
import kotlin.streams.asSequence
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class StreamsVsSequencesTest {
    @Test
    fun listOperationsAreEager() {
        val log = mutableListOf<String>()
        assertEquals(20, traceEager(listOf(1, 2, 3), log))
        assertEquals(listOf("map 1", "map 2", "map 3", "check 10", "check 20"), log)
    }

    @Test
    fun sequencesAndStreamsGoElementByElement() {
        val expected = listOf("map 1", "check 10", "map 2", "check 20")
        val sequenceLog = mutableListOf<String>()
        assertEquals(20, traceLazy(listOf(1, 2, 3), sequenceLog))
        assertEquals(expected, sequenceLog)
        val streamLog = mutableListOf<String>()
        assertEquals(20, traceStream(listOf(1, 2, 3), streamLog))
        assertEquals(expected, streamLog)
    }

    @Test
    fun streamsAreSingleUse() {
        val stream = Stream.of(1, 2, 3)
        assertEquals(3L, stream.count())
        assertFailsWith<IllegalStateException> { stream.count() }
        val sequence = listOf(1, 2, 3).asSequence()
        assertEquals(sequence.count(), sequence.count())
        val fromStream = Stream.of(1, 2).asSequence()
        assertEquals(2, fromStream.count())
        assertFailsWith<IllegalStateException> { fromStream.count() } // a one-shot source stays one-shot
    }

    @Test
    fun conversionsBothWays() {
        assertEquals(mapOf("kotlin" to 6, "java" to 4), wordLengths(Stream.of("kotlin", " ", "java")))
        assertEquals("[A, B]", javaStyleJoin(listOf("a", "b")))
        assertFailsWith<UnsupportedOperationException> { (Stream.of(1).toList() as MutableList<Int>).add(2) }
    }
}
