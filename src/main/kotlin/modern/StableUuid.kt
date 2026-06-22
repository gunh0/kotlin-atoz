package atoz.modern

import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

/*
`kotlin.uuid.Uuid` (stable in 2.4; experimental since 2.0.20): a UUID type in
the standard library, so common code no longer needs java.util.UUID.
Only the explicit generators `generateV4()`/`generateV7()` are still
experimental.
- `Uuid.random()` makes a version-4 UUID from the platform's secure random
  source, like Java's `UUID.randomUUID()`. Unguessable, but it is an
  identifier that ends up in logs and URLs: don't use it as a secret.
- `Uuid.parse` throws on malformed text; `parseOrNull` returns null, which
  suits untrusted input such as a request header. `toString()` is the
  lowercase hex-dash form, `toHexString()` the 32 digits without dashes.
- `toJavaUuid()` / `toKotlinUuid()` convert at the JVM boundary.
*/

@JvmInline
value class RequestId(val uuid: Uuid) {
    override fun toString() = "req-$uuid"

    companion object {
        fun next(): RequestId = RequestId(Uuid.random())

        // An attacker-controlled header: reject instead of throwing deep inside.
        fun fromHeader(raw: String?): RequestId? = raw?.let { Uuid.parseOrNull(it.trim()) }?.let(::RequestId)
    }
}

fun main() {
    val id = RequestId.next()
    println("new: $id (version ${id.uuid.toJavaUuid().version()})")
    println("from header: ${RequestId.fromHeader(" 550e8400-e29b-41d4-a716-446655440000 ")}")
    println("bad header: ${RequestId.fromHeader("550e8400' OR 1=1 --")}")
    println("hex: ${Uuid.parse("550e8400-e29b-41d4-a716-446655440000").toHexString()}, nil: ${Uuid.NIL}")
}
