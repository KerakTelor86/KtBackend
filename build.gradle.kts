plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
}

group = "me.keraktelor"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.cio.EngineMain"

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
    maven { url = uri("https://packages.confluent.io/maven/") }
}

dependencies {
    implementation(libs.ktor.server.rate.limiting)
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger.slf4j)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.h2)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.metrics)
    implementation(libs.ktor.server.call.id)
    implementation(libs.ktor.server.sse)
    implementation(libs.ktor.server.forwarded.header)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.compression)
    implementation(libs.ktor.server.resources)
    implementation(libs.ktor.server.request.validation)
    implementation(libs.ktor.server.auto.head.response)
    implementation(libs.ktor.server.csrf)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.ktor.server.cio)
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.config.yaml)
    implementation(libs.kotlinx.datetime)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)
}
