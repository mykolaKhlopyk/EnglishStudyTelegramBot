plugins {
    id("org.springframework.boot") version "3.1.3"
    id("io.spring.dependency-management") version "1.1.3"
    id("java-test-fixtures")
    kotlin("plugin.spring") version "1.9.0"
}

dependencies {

    implementation(project(":internal-api"))
    implementation(project(":common"))
    implementation(project(":library"))

    testFixturesApi("org.mockito:mockito-core:3.5.13")
    testFixturesApi("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.7.3")
    testFixturesApi("org.springframework.boot:spring-boot-starter-data-mongodb")

    testImplementation(testFixtures(project(":library")))
    testImplementation(project(":"))
}

tasks.test {
    useJUnitPlatform()
}
