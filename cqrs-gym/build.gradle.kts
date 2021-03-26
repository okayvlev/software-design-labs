import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ktorVersion: String by project

plugins {
    kotlin("jvm") version "1.4.31"
    kotlin("plugin.serialization") version "1.4.31"
}

group = "dev.okayvlev"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.1.0")
    implementation("org.litote.kmongo:kmongo:4.2.5")
    implementation("com.charleskorn.kaml:kaml:0.27.0")
    implementation("ch.qos.logback:logback-classic:1.2.3")

    testImplementation("junit:junit:4.12")
}


val compileKotlin: KotlinCompile by tasks
val compileTestKotlin: KotlinCompile by tasks

compileKotlin.apply {
    kotlinOptions.jvmTarget = "1.8"
}
compileTestKotlin.apply {
    kotlinOptions.jvmTarget = "1.8"
}
