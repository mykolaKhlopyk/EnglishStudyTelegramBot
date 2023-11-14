plugins {
    id("org.springframework.boot") version "3.1.3"
    id("io.spring.dependency-management") version "1.1.3"
    id("com.google.protobuf") version "0.9.4"
    kotlin("plugin.noarg") version "1.9.0"
    kotlin("plugin.spring") version "1.9.0"
}

dependencies {
    implementation("io.confluent:kafka-protobuf-serializer:7.5.1")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

noArg {
    annotation("systems.ajax.englishstudytelegrambot.annotation.LogMethodsByRequiredAnnotations")
}
