package atoz.functional

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class OperatorOverloadingTest {
    @Test
    fun arithmeticOperators() {
        val a = Vec2(1, 2)
        val b = Vec2(3, 4)
        assertEquals(Vec2(4, 6), a + b)
        assertEquals(Vec2(2, 2), b - a)
        assertEquals(Vec2(3, 6), a * 3)
        assertEquals(Vec2(-1, -2), -a)
        assertTrue(a < b)
    }

    @Test
    fun indexAndContains() {
        val grid = Grid(3, 2)
        grid[Vec2(2, 1)] = '#'
        assertEquals('#', grid[Vec2(2, 1)])
        assertEquals("...\n..#", grid.render())
        assertTrue(Vec2(0, 0) in grid)
        assertFalse(Vec2(3, 0) in grid)
        assertFailsWith<IllegalArgumentException> { grid[Vec2(3, 0)] }
    }

    @Test
    fun invokeAndPlusAssign() {
        assertEquals(19, Polynomial(1, 0, 2)(3))
        val ledger = Ledger()
        ledger += 100
        ledger += -30
        assertEquals(listOf(100, -30), ledger.entries)
    }
}
