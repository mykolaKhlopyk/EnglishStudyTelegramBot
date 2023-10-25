plugins {
    id("org.springframework.boot") version "3.1.3"
    id("io.spring.dependency-management") version "1.1.3"
    kotlin("jvm") version "1.9.0"
    kotlin("plugin.noarg") version "1.9.0"
    kotlin("plugin.spring") version "1.9.0"
    id("io.gitlab.arturbosch.detekt") version "1.23.1"
}

dependencies {
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
    implementation ("io.projectreactor.kotlin:reactor-kotlin-extensions:1.2.2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

noArg {
    annotation("systems.ajax.englishstudytelegrambot.annotation.LogMethodsByRequiredAnnotations")
}

/*
    - for first version of integration reactor, nats parts are excluded
    - in next pr, this excluding will be deleted
 */
sourceSets {
    main {
        kotlin {
            exclude("**/systems/ajax/englishstudytelegrambot/nats/**")
            exclude("**/systems/ajax/englishstudytelegrambot/bpp/NatsControllerHandlerBeanPostProcessor.kt")
        }
    }
    test {
        kotlin {
            exclude("**/systems/ajax/englishstudytelegrambot/NatsControllerTest.kt")
        }
    }
}
