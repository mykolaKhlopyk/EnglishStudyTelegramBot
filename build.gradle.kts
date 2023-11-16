import com.google.protobuf.gradle.id
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    java
    id("org.springframework.boot") version "3.1.3"
    id("io.spring.dependency-management") version "1.1.3"
    id("com.google.protobuf") version "0.9.4"
    id("io.gitlab.arturbosch.detekt") version "1.23.1"
    kotlin("jvm") version "1.9.0"
    kotlin("plugin.serialization") version "1.9.0"
    kotlin("plugin.noarg") version "1.9.0"
    kotlin("plugin.spring") version "1.9.0"
}

tasks.withType<BootJar> {
    mainClass.set("systems.ajax.EnglishStudyTelegramBotApplicationKt")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")

    subprojects.forEach {
        implementation(it)
    }
}

subprojects {
    apply(plugin = "kotlin")
    apply(plugin = "com.google.protobuf")

    dependencies {

        //spring
        implementation("org.springframework.boot:spring-boot-starter-web")
        implementation("org.springframework.boot:spring-boot-starter-validation:3.1.3")

        //nats
        implementation("io.nats:jnats:2.16.14")

        //redis
        implementation("io.lettuce:lettuce-core:6.2.6.RELEASE")
        implementation("org.springframework.data:spring-data-redis:3.1.5")

        //kafka
        implementation("org.springframework.kafka:spring-kafka")
        implementation("io.projectreactor.kafka:reactor-kafka:1.3.21")
        implementation("io.confluent:kafka-protobuf-serializer:7.5.1")

        //reactor
        implementation("org.springframework.boot:spring-boot-starter-webflux:3.1.3")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.7.3")
        implementation("io.projectreactor:reactor-core:3.5.11")
        implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.2.2")

        //proto
        implementation("com.google.protobuf:protobuf-java:3.25.0")

        //grpc
        implementation("net.devh:grpc-spring-boot-starter:2.15.0.RELEASE")
        implementation("io.grpc:grpc-protobuf:1.59.0")
        implementation("io.grpc:grpc-stub:1.59.0")
        implementation("io.grpc:grpc-netty-shaded:1.59.0")
        implementation("com.salesforce.servicelibs:reactor-grpc:1.2.4")
        implementation("com.salesforce.servicelibs:reactive-grpc-common:1.2.4")
        implementation("com.salesforce.servicelibs:reactor-grpc-stub:1.2.4")

        //mongo
        implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive:3.1.5")
        implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
        implementation("org.mongodb:bson:4.11.1")

        //serialization
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

        //test
        testImplementation("io.mockk:mockk:1.12.0")
        testImplementation("io.projectreactor:reactor-test:3.5.11")
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.3.1")
        testImplementation("org.junit.jupiter:junit-jupiter-engine:5.3.1")
        testImplementation("org.springframework.boot:spring-boot-starter-test")
    }

    protobuf {
        protoc {
            artifact = "com.google.protobuf:protoc:3.21.7"
        }
        plugins {
            id("grpc") {
                artifact = "io.grpc:protoc-gen-grpc-java:1.49.2"
            }
            id("reactor") {
                artifact = "com.salesforce.servicelibs:reactor-grpc:1.2.4"
            }
        }
        generateProtoTasks {
            ofSourceSet("main").forEach {
                it.plugins {
                    id("grpc")
                    id("reactor")
                }
            }
        }
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs += "-Xjsr305=strict"
            jvmTarget = "20"
        }
    }

    tasks.withType<BootJar> {
        enabled = false
    }
}

allprojects {
    group = "systems.ajax"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
        maven {
            url = uri("https://packages.confluent.io/maven/")
        }
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_20
    }
}
