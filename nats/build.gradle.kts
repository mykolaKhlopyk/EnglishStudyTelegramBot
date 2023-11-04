import com.google.protobuf.gradle.id

plugins {
    id("java")
}

group = "systems.ajax"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

tasks.test {
    useJUnitPlatform()
}
