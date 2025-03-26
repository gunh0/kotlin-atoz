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
- String templates, raw strings, `trimIndent`, `buildString`: [`Strings.kt`](src/main/kotlin/basics/Strings.kt)
<!-- INDEX:basics:END -->

### oop

Classes and constructors, properties, data/sealed/enum classes, objects and companions, interfaces, delegation.

<!-- INDEX:oop:START -->
- Primary and secondary constructors, default values: [`Constructors.kt`](src/main/kotlin/oop/Constructors.kt)
- Initialization order, `init` blocks, open calls in constructors: [`InitOrder.kt`](src/main/kotlin/oop/InitOrder.kt)
<!-- INDEX:oop:END -->

### functional

Lambdas, higher-order and inline functions, scope functions, extension functions.

<!-- INDEX:functional:START -->
- Lambdas and function types: [`Lambdas.kt`](src/main/kotlin/functional/Lambdas.kt)
- Higher-order functions, trailing lambdas: [`HigherOrderFunctions.kt`](src/main/kotlin/functional/HigherOrderFunctions.kt)
<!-- INDEX:functional:END -->

### collections

Read-only vs mutable collections, transformations, grouping, sequences.

<!-- INDEX:collections:START -->
- Read-only vs mutable collections, defensive copies: [`ReadOnlyVsMutable.kt`](src/main/kotlin/collections/ReadOnlyVsMutable.kt)
- Collection factories, `buildList`/`buildMap`: [`CollectionBuilders.kt`](src/main/kotlin/collections/CollectionBuilders.kt)
<!-- INDEX:collections:END -->

### generics

Generic functions and classes, variance (`in`/`out`), reified type parameters.

<!-- INDEX:generics:START -->
- Generic functions and classes, `T & Any`: [`GenericBasics.kt`](src/main/kotlin/generics/GenericBasics.kt)
<!-- INDEX:generics:END -->

### coroutines

Structured concurrency, cancellation, `async`, `Flow`, channels, testing with virtual time.

<!-- INDEX:coroutines:START -->
- Suspend functions, `runBlocking`, the hidden `Continuation` parameter: [`SuspendFunctions.kt`](src/main/kotlin/coroutines/SuspendFunctions.kt)
- `launch` vs `async`/`await`, sequential vs concurrent calls: [`LaunchAsync.kt`](src/main/kotlin/coroutines/LaunchAsync.kt)
- Structured concurrency: `coroutineScope` waits for children, failures cancel siblings: [`StructuredConcurrency.kt`](src/main/kotlin/coroutines/StructuredConcurrency.kt)
<!-- INDEX:coroutines:END -->

### dsl

Type-safe builders, lambdas with receivers, `@DslMarker`.

<!-- INDEX:dsl:START -->
- Lambdas with receiver: `T.() -> Unit`: [`LambdasWithReceiver.kt`](src/main/kotlin/dsl/LambdasWithReceiver.kt)
<!-- INDEX:dsl:END -->

### interop

Calling Java from Kotlin and Kotlin from Java: platform types, `@Jvm*` annotations, SAM conversions.

<!-- INDEX:interop:START -->
- Platform types (`String!`) and nullability at the Java boundary: [`PlatformTypes.kt`](src/main/kotlin/interop/PlatformTypes.kt)
<!-- INDEX:interop:END -->

### modern

Language features by Kotlin release, starting with 2.x.

<!-- INDEX:modern:START -->
<!-- INDEX:modern:END -->

### secure

Secure coding in Kotlin: what null safety and immutability buy you, and what they don't.

<!-- INDEX:secure:START -->
- Validating input at the boundary: `requireNotNull`, `require`, `check`: [`InputValidation.kt`](src/main/kotlin/secure/InputValidation.kt)
<!-- INDEX:secure:END -->
