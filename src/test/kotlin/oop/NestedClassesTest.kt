package atoz.oop

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class NestedClassesTest {
    @Test
    fun nestedClassNeedsNoOuterInstance() {
        val track = Playlist.Track("Intro", 90)
        assertEquals("Intro", track.title)
    }

    @Test
    fun innerClassSeesOuterState() {
        val playlist = Playlist("focus").add(Playlist.Track("a", 10)).add(Playlist.Track("b", 20))
        val cursor = playlist.Cursor()
        assertEquals("focus", cursor.owner())
        assertEquals("a", cursor.current()?.title)
        assertEquals("b", cursor.next()?.title)
        assertNull(cursor.next())
        assertEquals(30, playlist.totalSeconds())
    }

    @Test
    fun eachCursorBelongsToItsOwnPlaylist() {
        val one = Playlist("one").add(Playlist.Track("x", 1))
        val two = Playlist("two")
        assertEquals("x", one.Cursor().current()?.title)
        assertNull(two.Cursor().current())
    }
}
