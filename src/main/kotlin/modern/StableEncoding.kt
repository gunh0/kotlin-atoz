package atoz.modern

import kotlin.io.encoding.Base64

/*
Base64 and HexFormat in the standard library (stable in 2.2): until 2.2 they
needed an opt-in (@ExperimentalEncodingApi, @ExperimentalStdlibApi), so JVM
code reached for java.util.Base64 and java.util.HexFormat instead. Now plain
Kotlin has them, on every platform:
- Base64.Default, Base64.UrlSafe and Base64.Mime, with
  `withPadding(Base64.PaddingOption.ABSENT)` for tokens in URLs; `decode`
  rejects malformed input with IllegalArgumentException.
- `ByteArray.toHexString()` / `String.hexToByteArray()` and the same for
  numbers, with a `HexFormat { }` builder for case, separators and prefixes
  (handy for certificate and key fingerprints).
*/

private val urlToken = Base64.UrlSafe.withPadding(Base64.PaddingOption.ABSENT)

fun encodeToken(bytes: ByteArray): String = urlToken.encode(bytes)

fun decodeToken(token: String): ByteArray = urlToken.decode(token)

private val fingerprintFormat = HexFormat {
    upperCase = true
    bytes.byteSeparator = ":"
}

fun fingerprint(digest: ByteArray): String = digest.toHexString(fingerprintFormat)

fun parseHex(text: String): ByteArray = text.hexToByteArray()

fun main() {
    val bytes = byteArrayOf(-5, -17, 0, 42)
    println("Default: ${Base64.Default.encode(bytes)}  UrlSafe/no padding: ${encodeToken(bytes)}")
    println("decoded back: ${decodeToken(encodeToken(bytes)).contentToString()}")
    println("hex: ${bytes.toHexString()}  fingerprint: ${fingerprint(bytes)}")
    println("0xff as hex: ${255.toHexString()}")
}
