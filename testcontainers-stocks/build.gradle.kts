import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

val ktorVersion: String by project

plugins {
    kotlin("jvm") version "1.4.31"
    kotlin("plugin.serialization") version "1.4.31"
    id("com.github.johnrengelman.shadow") version "6.1.0"
    application

}

group = "dev.okayvlev"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-serialization:$ktorVersion")
    implementation("io.ktor:ktor-client-serialization:$ktorVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.1.0")
    implementation("com.charleskorn.kaml:kaml:0.27.0")
    implementation("ch.qos.logback:logback-classic:1.2.3")

    testImplementation("junit:junit:4.12")
    testImplementation("org.testcontainers:testcontainers:1.15.2")
}

application {
    mainClassName = "services.exchange.StockExchangeAppKt"
}

tasks.withType<ShadowJar> {
    archiveFileName.set("${project.name}.jar")
}
val compileKotlin: KotlinCompile by tasks
val compileTestKotlin: KotlinCompile by tasks

compileKotlin.apply {
    kotlinOptions.jvmTarget = "1.8"
}
compileTestKotlin.apply {
    kotlinOptions.jvmTarget = "1.8"
}
