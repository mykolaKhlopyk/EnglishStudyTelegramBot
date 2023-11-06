import com.google.protobuf.gradle.id
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.0"
    id("com.google.protobuf") version "0.9.4"
}

allprojects {
    group = "systems.ajax"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }
    apply(plugin = "kotlin")
    apply(plugin = "com.google.protobuf")

    dependencies {
        implementation("com.salesforce.servicelibs:reactor-grpc:1.2.4")
        implementation("com.salesforce.servicelibs:reactive-grpc-common:1.2.4")
        implementation("com.salesforce.servicelibs:reactor-grpc-stub:1.2.4")
        implementation("io.projectreactor:reactor-core:3.5.11")
        implementation("net.devh:grpc-spring-boot-starter:2.15.0.RELEASE")
        implementation("io.grpc:grpc-protobuf:1.59.0")
        implementation("io.grpc:grpc-stub:1.59.0")
        implementation("io.grpc:grpc-netty-shaded:1.59.0")
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
            jvmTarget = "17"
        }
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_17
    }
}
