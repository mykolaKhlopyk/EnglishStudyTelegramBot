plugins {
    id("java")
    id("org.springframework.boot") version "3.1.3"
    id("io.spring.dependency-management") version "1.1.3"
    id("java-test-fixtures")
    id("io.gitlab.arturbosch.detekt") version "1.23.1"
    kotlin("jvm") version "1.9.0"
    kotlin("plugin.serialization") version "1.9.10"
    kotlin("plugin.noarg") version "1.9.0"
    kotlin("plugin.spring") version "1.9.0"
}

dependencies {
    testFixturesApi("org.mockito:mockito-core:3.5.13")
    testFixturesApi("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.7.3")
    testFixturesApi("org.springframework.boot:spring-boot-starter-data-mongodb")
    testRuntimeOnly ("org.junit.jupiter:junit-jupiter-engine:5.3.1")
    implementation(project(":nats"))
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.7.3")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("io.nats:jnats:2.16.14")
    implementation("com.google.protobuf:protobuf-java:3.24.3")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.boot:spring-boot-starter-webflux:3.1.3")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.1.3")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive:3.1.5")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.2.2")
    implementation("io.lettuce:lettuce-core:6.2.6.RELEASE")
    implementation("org.springframework.data:spring-data-redis:3.1.5")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
    testImplementation("io.mockk:mockk:1.12.0")
    testImplementation("io.projectreactor:reactor-test:3.5.11")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.3.1")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    api("org.apache.commons:commons-lang3:3.9")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

noArg {
    annotation("systems.ajax.englishstudytelegrambot.annotation.LogMethodsByRequiredAnnotations")
}
