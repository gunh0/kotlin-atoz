# Kotlin A to Z

A Kotlin study notebook: one small, runnable example per concept, each with a test that pins down the behaviour it describes. The companion of [java-atoz](https://github.com/gunh0/java-atoz), written from the point of view of someone who knows Java and wants to see what Kotlin changes.

## Running

```bash
./gradlew test                                   # compile everything and run all tests
./gradlew run -Pmain=atoz.basics.VariablesKt     # run one example's main()
```

Requires JDK 21 (Gradle downloads it through the toolchain resolver if it isn't installed).

Layout: `src/main/kotlin/<chapter>/<Topic>.kt` holds the example, `src/test/kotlin/<chapter>/<Topic>Test.kt` its test. The class to pass to `-Pmain` is the package plus the file name with a `Kt` suffix.

## Chapters

### basics

Values and variables, types, strings, control flow, functions, null safety.

<!-- INDEX:basics:START -->
- Hello, Kotlin: [`HelloKotlin.kt`](src/main/kotlin/basics/HelloKotlin.kt)
- `val` vs `var`, nullable types: [`Variables.kt`](src/main/kotlin/basics/Variables.kt)
- Basic types, no implicit widening, explicit conversions: [`BasicTypes.kt`](src/main/kotlin/basics/BasicTypes.kt)
<!-- INDEX:basics:END -->

### oop

Classes and constructors, properties, data/sealed/enum classes, objects and companions, interfaces, delegation.

<!-- INDEX:oop:START -->
<!-- INDEX:oop:END -->

### functional

Lambdas, higher-order and inline functions, scope functions, extension functions.

<!-- INDEX:functional:START -->
<!-- INDEX:functional:END -->

### collections

Read-only vs mutable collections, transformations, grouping, sequences.

<!-- INDEX:collections:START -->
<!-- INDEX:collections:END -->

### generics

Generic functions and classes, variance (`in`/`out`), reified type parameters.

<!-- INDEX:generics:START -->
<!-- INDEX:generics:END -->

### coroutines

Structured concurrency, cancellation, `async`, `Flow`, channels, testing with virtual time.

<!-- INDEX:coroutines:START -->
<!-- INDEX:coroutines:END -->

### dsl

Type-safe builders, lambdas with receivers, `@DslMarker`.

<!-- INDEX:dsl:START -->
<!-- INDEX:dsl:END -->

### interop

Calling Java from Kotlin and Kotlin from Java: platform types, `@Jvm*` annotations, SAM conversions.

<!-- INDEX:interop:START -->
<!-- INDEX:interop:END -->

### modern

Language features by Kotlin release, starting with 2.x.

<!-- INDEX:modern:START -->
<!-- INDEX:modern:END -->

### secure

Secure coding in Kotlin: what null safety and immutability buy you, and what they don't.

<!-- INDEX:secure:START -->
<!-- INDEX:secure:END -->
