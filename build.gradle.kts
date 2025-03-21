plugins {
    kotlin("jvm") version "2.1.20"
    application
}

group = "io.github.gunh0"
version = "0.1.0"

repositories {
    mavenCentral()
}

val coroutinesVersion = "1.10.1"

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}

// Run any example: ./gradlew run -Pmain=atoz.basics.HelloKotlinKt
application {
    mainClass.set(providers.gradleProperty("main").orElse("atoz.basics.HelloKotlinKt"))
}
