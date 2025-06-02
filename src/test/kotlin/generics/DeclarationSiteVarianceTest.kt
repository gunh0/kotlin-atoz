package atoz.generics

import kotlin.test.Test
import kotlin.test.assertEquals

class DeclarationSiteVarianceTest {
    @Test
    fun outMakesProducersCovariant() {
        val strings: Producer<String> = ConstantProducer("token")
        val anything: Producer<Any> = strings
        assertEquals("token", anything.produce())
        assertEquals(listOf("token", "42"), describeAll(listOf(strings, ConstantProducer(42))))
    }

    @Test
    fun inMakesConsumersContravariant() {
        val anyConsumer = CollectingConsumer<Any>()
        val stringConsumer: Consumer<String> = anyConsumer
        stringConsumer.consume("a")
        feedStrings(anyConsumer, listOf("b"))
        assertEquals(listOf("a", "b"), anyConsumer.seen())
    }

    @Test
    fun readOnlyListIsCovariant() {
        val names: List<String> = listOf("x", "y")
        assertEquals(2, countAll(names))
    }

    @Test
    fun comparatorIsContravariant() {
        val byHash: Comparator<Any> = compareBy { it.hashCode() }
        val words = listOf("b", "a", "c")
        assertEquals(listOf("a", "b", "c"), words.sortedWith(byHash))
    }
}
