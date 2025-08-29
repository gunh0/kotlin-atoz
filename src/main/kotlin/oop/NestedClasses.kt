package atoz.oop

// A class declared inside another is *nested* by default: it has no reference
// to an outer instance, like a `static` nested class in Java. Marking it
// `inner` adds that reference (Java's default), and `this@Outer` names it.
//
// Kotlin picks the safer default on purpose: an inner class instance keeps its
// outer object alive, a classic source of memory leaks (listeners, Android
// activities). Use `inner` only when the class really needs outer state.

class Playlist(val name: String) {
    private val tracks = mutableListOf<Track>()

    class Track(val title: String, val seconds: Int) // nested: no Playlist needed

    inner class Cursor { // inner: bound to one Playlist
        private var index = 0
        fun current(): Track? = tracks.getOrNull(index)
        fun next(): Track? = tracks.getOrNull(++index)
        fun owner(): String = this@Playlist.name
    }

    fun add(track: Track): Playlist = apply { tracks += track }
    fun totalSeconds(): Int = tracks.sumOf { it.seconds }
}

fun main() {
    val track = Playlist.Track("Intro", 90) // created without any Playlist
    val playlist = Playlist("focus").add(track).add(Playlist.Track("Deep work", 1500))
    val cursor = playlist.Cursor() // needs an instance
    println("${cursor.owner()}: ${cursor.current()?.title} -> ${cursor.next()?.title} -> ${cursor.next()?.title}")
    println("total: ${playlist.totalSeconds()}s")
}
