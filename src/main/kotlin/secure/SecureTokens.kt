package atoz.secure

import java.security.SecureRandom
import java.util.Base64
import kotlin.random.Random
import kotlin.random.asKotlinRandom

/*
`kotlin.random.Random` (like java.util.Random) is a fast pseudo-random
generator: given the seed, or a few outputs, the whole sequence can be
reproduced. Fine for shuffling test data, never for session IDs, password
reset tokens, API keys or nonces.
Use java.security.SecureRandom, which draws from the OS CSPRNG. Generate at
least 128 bits (here 256) and encode them URL-safe without padding so the
token can go into links and headers unchanged.
`SecureRandom().asKotlinRandom()` gives Kotlin's Random API on top of a
secure source when you need ranges or shuffles that must be unpredictable.
*/

private val secureRandom = SecureRandom()

fun newToken(byteCount: Int = 32): String {
    require(byteCount >= 16) { "tokens need at least 128 bits" }
    val bytes = ByteArray(byteCount).also(secureRandom::nextBytes)
    return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
}

// Predictable on purpose: two generators with the same seed agree forever.
fun predictableCodes(seed: Int, count: Int): List<Int> {
    val random = Random(seed)
    return List(count) { random.nextInt(100_000, 1_000_000) }
}

fun secureOneTimeCode(): Int = secureRandom.asKotlinRandom().nextInt(100_000, 1_000_000)

fun main() {
    println("token: ${newToken()}")
    println("seeded codes: ${predictableCodes(seed = 42, count = 3)}")
    println("seeded again: ${predictableCodes(seed = 42, count = 3)}")
    println("secure code: ${secureOneTimeCode()}")
}
