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
- `if` and `when` as expressions: [`Conditionals.kt`](src/main/kotlin/basics/Conditionals.kt)
- Ranges and progressions: `..`, `..<`, `downTo`, `step`: [`Ranges.kt`](src/main/kotlin/basics/Ranges.kt)
- Loops, labels, `return@forEach`: [`Loops.kt`](src/main/kotlin/basics/Loops.kt)
- Functions: default/named arguments, single-expression, `vararg`: [`Functions.kt`](src/main/kotlin/basics/Functions.kt)
- Null safety: `?.`, `?:`, `!!`, smart casts: [`NullSafety.kt`](src/main/kotlin/basics/NullSafety.kt)
- Safe call chains, `?.let`, `takeIf`, `mapNotNull`: [`SafeCalls.kt`](src/main/kotlin/basics/SafeCalls.kt)
- Equality: `==` vs `===`, floating-point edge cases: [`Equality.kt`](src/main/kotlin/basics/Equality.kt)
- Exceptions: `try` as an expression, `require`/`check`/`error`, `use`: [`Exceptions.kt`](src/main/kotlin/basics/Exceptions.kt)
- Destructuring declarations and `componentN`: [`Destructuring.kt`](src/main/kotlin/basics/Destructuring.kt)
- Type checks and casts: `is`, `as`, `as?`: [`TypeChecks.kt`](src/main/kotlin/basics/TypeChecks.kt)
<!-- INDEX:basics:END -->

### oop

Classes and constructors, properties, data/sealed/enum classes, objects and companions, interfaces, delegation.

<!-- INDEX:oop:START -->
- Primary and secondary constructors, default values: [`Constructors.kt`](src/main/kotlin/oop/Constructors.kt)
- Initialization order, `init` blocks, open calls in constructors: [`InitOrder.kt`](src/main/kotlin/oop/InitOrder.kt)
- Properties: custom accessors, backing field, `lateinit`, `by lazy`: [`Properties.kt`](src/main/kotlin/oop/Properties.kt)
- Visibility: `private`, `protected`, `internal`, private constructors: [`Visibility.kt`](src/main/kotlin/oop/Visibility.kt)
- Data classes: `copy`, `componentN`, generated `toString` leaks: [`DataClasses.kt`](src/main/kotlin/oop/DataClasses.kt)
- Sealed types, exhaustive `when`, `data object`: [`SealedClasses.kt`](src/main/kotlin/oop/SealedClasses.kt)
- Enum classes: properties, per-constant overrides, `entries`: [`EnumClasses.kt`](src/main/kotlin/oop/EnumClasses.kt)
- `object` singletons, companion factories, object expressions: [`Objects.kt`](src/main/kotlin/oop/Objects.kt)
- Interfaces: default implementations, properties, `super<T>`: [`Interfaces.kt`](src/main/kotlin/oop/Interfaces.kt)
- Abstract classes, `open`, template method: [`AbstractClasses.kt`](src/main/kotlin/oop/AbstractClasses.kt)
- Class delegation with `by`: [`ClassDelegation.kt`](src/main/kotlin/oop/ClassDelegation.kt)
- Property delegation: `observable`, `vetoable`, `notNull`, map, custom: [`PropertyDelegation.kt`](src/main/kotlin/oop/PropertyDelegation.kt)
<!-- INDEX:oop:END -->

### functional

Lambdas, higher-order and inline functions, scope functions, extension functions.

<!-- INDEX:functional:START -->
- Lambdas and function types: [`Lambdas.kt`](src/main/kotlin/functional/Lambdas.kt)
- Higher-order functions, trailing lambdas: [`HigherOrderFunctions.kt`](src/main/kotlin/functional/HigherOrderFunctions.kt)
- `inline`, `noinline`, `crossinline`, non-local returns: [`InlineFunctions.kt`](src/main/kotlin/functional/InlineFunctions.kt)
- Scope functions: `let`, `run`, `with`, `apply`, `also`: [`ScopeFunctions.kt`](src/main/kotlin/functional/ScopeFunctions.kt)
- Extension functions and properties, static dispatch: [`Extensions.kt`](src/main/kotlin/functional/Extensions.kt)
- Function, property and constructor references (`::`): [`FunctionReferences.kt`](src/main/kotlin/functional/FunctionReferences.kt)
- Closures and captured variables: [`Closures.kt`](src/main/kotlin/functional/Closures.kt)
- `tailrec` functions: [`TailRecursion.kt`](src/main/kotlin/functional/TailRecursion.kt)
- Operator overloading: [`OperatorOverloading.kt`](src/main/kotlin/functional/OperatorOverloading.kt)
- Infix functions and bitwise operators: [`InfixFunctions.kt`](src/main/kotlin/functional/InfixFunctions.kt)
- `fold`, `reduce`, `runningFold`, `foldRight`: [`FoldReduce.kt`](src/main/kotlin/functional/FoldReduce.kt)
- Currying and partial application: [`Currying.kt`](src/main/kotlin/functional/Currying.kt)
- Memoization, `getOrPut` vs `computeIfAbsent`: [`Memoization.kt`](src/main/kotlin/functional/Memoization.kt)
<!-- INDEX:functional:END -->

### collections

Read-only vs mutable collections, transformations, grouping, sequences.

<!-- INDEX:collections:START -->
- Read-only vs mutable collections, defensive copies: [`ReadOnlyVsMutable.kt`](src/main/kotlin/collections/ReadOnlyVsMutable.kt)
- Collection factories, `buildList`/`buildMap`: [`CollectionBuilders.kt`](src/main/kotlin/collections/CollectionBuilders.kt)
- `map`, `filter`, `flatMap` and friends: [`Transformations.kt`](src/main/kotlin/collections/Transformations.kt)
- `groupBy`, `groupingBy`, `associate*`, `partition`: [`Grouping.kt`](src/main/kotlin/collections/Grouping.kt)
- `windowed`, `chunked`, `zip`, `zipWithNext`: [`Windowing.kt`](src/main/kotlin/collections/Windowing.kt)
- Sorting: `sortedBy`, `compareBy`, `thenBy`, `nullsLast`: [`Sorting.kt`](src/main/kotlin/collections/Sorting.kt)
- Set operations and map lookups: [`SetsAndMaps.kt`](src/main/kotlin/collections/SetsAndMaps.kt)
- Sequences: lazy vs eager, `generateSequence`, `sequence {}`: [`Sequences.kt`](src/main/kotlin/collections/Sequences.kt)
- Arrays vs lists, `contentEquals`, wiping a `ByteArray`: [`ArraysVsLists.kt`](src/main/kotlin/collections/ArraysVsLists.kt)
<!-- INDEX:collections:END -->

### generics

Generic functions and classes, variance (`in`/`out`), reified type parameters.

<!-- INDEX:generics:START -->
- Generic functions and classes, `T & Any`: [`GenericBasics.kt`](src/main/kotlin/generics/GenericBasics.kt)
- Upper bounds and `where` clauses: [`GenericConstraints.kt`](src/main/kotlin/generics/GenericConstraints.kt)
- Declaration-site variance: `out` and `in`: [`DeclarationSiteVariance.kt`](src/main/kotlin/generics/DeclarationSiteVariance.kt)
- Use-site variance: type projections, `*`: [`UseSiteVariance.kt`](src/main/kotlin/generics/UseSiteVariance.kt)
- `reified` type parameters: [`ReifiedTypes.kt`](src/main/kotlin/generics/ReifiedTypes.kt)
<!-- INDEX:generics:END -->

### coroutines

Structured concurrency, cancellation, `async`, `Flow`, channels, testing with virtual time.

<!-- INDEX:coroutines:START -->
- Suspend functions, `runBlocking`, the hidden `Continuation` parameter: [`SuspendFunctions.kt`](src/main/kotlin/coroutines/SuspendFunctions.kt)
- `launch` vs `async`/`await`, sequential vs concurrent calls: [`LaunchAsync.kt`](src/main/kotlin/coroutines/LaunchAsync.kt)
- Structured concurrency: `coroutineScope` waits for children, failures cancel siblings: [`StructuredConcurrency.kt`](src/main/kotlin/coroutines/StructuredConcurrency.kt)
- Cooperative cancellation, `ensureActive`, `NonCancellable` cleanup: [`Cancellation.kt`](src/main/kotlin/coroutines/Cancellation.kt)
- Timeouts: `withTimeout`, `withTimeoutOrNull`: [`Timeouts.kt`](src/main/kotlin/coroutines/Timeouts.kt)
- Exceptions: `launch` vs `async`, `supervisorScope`, `CoroutineExceptionHandler`: [`ExceptionHandling.kt`](src/main/kotlin/coroutines/ExceptionHandling.kt)
- Dispatchers and `withContext`, executors as dispatchers: [`Dispatchers.kt`](src/main/kotlin/coroutines/Dispatchers.kt)
- `Flow` basics: cold streams, `map`/`filter`, terminal operators: [`FlowBasics.kt`](src/main/kotlin/coroutines/FlowBasics.kt)
- Flow operators: `zip`, `combine`, `buffer`, `conflate`: [`FlowOperators.kt`](src/main/kotlin/coroutines/FlowOperators.kt)
- Flow errors: `catch`, `retry`, `onCompletion`, exception transparency: [`FlowErrors.kt`](src/main/kotlin/coroutines/FlowErrors.kt)
- `StateFlow` and `SharedFlow`: state vs events: [`StateAndSharedFlow.kt`](src/main/kotlin/coroutines/StateAndSharedFlow.kt)
- Channels: capacity, producer/consumer, fan-out: [`Channels.kt`](src/main/kotlin/coroutines/Channels.kt)
- Shared mutable state: `Mutex`, atomics: [`SharedState.kt`](src/main/kotlin/coroutines/SharedState.kt)
<!-- INDEX:coroutines:END -->

### dsl

Type-safe builders, lambdas with receivers, `@DslMarker`.

<!-- INDEX:dsl:START -->
- Lambdas with receiver: `T.() -> Unit`: [`LambdasWithReceiver.kt`](src/main/kotlin/dsl/LambdasWithReceiver.kt)
- Type-safe HTML builder that escapes text: [`HtmlBuilder.kt`](src/main/kotlin/dsl/HtmlBuilder.kt)
- `@DslMarker`: stopping outer receivers from leaking: [`ScopeControl.kt`](src/main/kotlin/dsl/ScopeControl.kt)
- Operator-based route DSL: `invoke`, `infix`, `get`, `contains`: [`RouteDsl.kt`](src/main/kotlin/dsl/RouteDsl.kt)
<!-- INDEX:dsl:END -->

### interop

Calling Java from Kotlin and Kotlin from Java: platform types, `@Jvm*` annotations, SAM conversions.

<!-- INDEX:interop:START -->
- Platform types (`String!`) and nullability at the Java boundary: [`PlatformTypes.kt`](src/main/kotlin/interop/PlatformTypes.kt)
- `@JvmStatic`, `@JvmField`, `@JvmOverloads`, `@JvmName` seen via reflection: [`JvmAnnotations.kt`](src/main/kotlin/interop/JvmAnnotations.kt)
- SAM conversions: `Runnable`, `Comparator`, `java.util.function`, `fun interface`: [`SamConversions.kt`](src/main/kotlin/interop/SamConversions.kt)
- Checked exceptions and `@Throws`: [`CheckedExceptions.kt`](src/main/kotlin/interop/CheckedExceptions.kt)
- Java records and `@JvmRecord` data classes: [`Records.kt`](src/main/kotlin/interop/Records.kt)
- Java streams vs Kotlin sequences: [`StreamsVsSequences.kt`](src/main/kotlin/interop/StreamsVsSequences.kt)
- `java.time` from Kotlin: operators, ranges, injected `Clock`: [`JavaTime.kt`](src/main/kotlin/interop/JavaTime.kt)
<!-- INDEX:interop:END -->

### modern

Language features by Kotlin release, starting with 2.x.

<!-- INDEX:modern:START -->
- Guard conditions in `when` (2.2): [`WhenGuards.kt`](src/main/kotlin/modern/WhenGuards.kt)
<!-- INDEX:modern:END -->

### secure

Secure coding in Kotlin: what null safety and immutability buy you, and what they don't.

<!-- INDEX:secure:START -->
- Validating input at the boundary: `requireNotNull`, `require`, `check`: [`InputValidation.kt`](src/main/kotlin/secure/InputValidation.kt)
- Sealed result types instead of exceptions for expected failures: [`SealedResults.kt`](src/main/kotlin/secure/SealedResults.kt)
- Defensive copies: read-only is not immutable: [`DefensiveCopies.kt`](src/main/kotlin/secure/DefensiveCopies.kt)
- Keeping secrets out of `toString()`: override, value class wrapper: [`SecretToString.kt`](src/main/kotlin/secure/SecretToString.kt)
- Constant-time comparison with `MessageDigest.isEqual`: [`ConstantTimeCompare.kt`](src/main/kotlin/secure/ConstantTimeCompare.kt)
- `SecureRandom` tokens vs predictable `kotlin.random.Random`: [`SecureTokens.kt`](src/main/kotlin/secure/SecureTokens.kt)
- Injection: bound parameters, `Regex.escape`, process argument lists: [`InjectionSafety.kt`](src/main/kotlin/secure/InjectionSafety.kt)
<!-- INDEX:secure:END -->
