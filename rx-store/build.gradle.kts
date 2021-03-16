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
    implementation("io.netty:netty-all:4.1.60.Final")
    implementation("io.reactivex:rxnetty-http:0.5.3")
    implementation("io.reactivex:rxnetty-tcp:0.5.3")
    implementation("io.reactivex:rxnetty-common:0.5.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.1.0")
    implementation("com.charleskorn.kaml:kaml:0.27.0")
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("io.reactivex.rxjava3:rxkotlin:3.0.1")
    implementation("org.mongodb:mongodb-driver-rx:1.5.0")
}
