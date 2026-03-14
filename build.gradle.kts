plugins {
    kotlin("jvm") version "1.9.23"
    application
}

group = "com.gradecalculator"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
}

application {
    mainClass.set("MainKt")
}

// No jvmToolchain — uses whatever JDK IntelliJ/your machine already has
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.current().toString()
}
