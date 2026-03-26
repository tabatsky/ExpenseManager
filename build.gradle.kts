plugins {
    kotlin("multiplatform")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
    id("androidx.room")
    kotlin("plugin.serialization") version "2.0.21"
    id("com.google.devtools.ksp") version "2.0.21-1.0.27"
}

group = "jatx.expense.manager"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    jvm {
        withJava()
    }
    jvmToolchain(22)
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("androidx.room:room-runtime:2.7.0-alpha01")
                implementation("androidx.sqlite:sqlite-bundled:2.5.0-alpha01")
                implementation("androidx.sqlite:sqlite:2.5.0-alpha01")
                implementation("dev.gitlive:firebase-common:2.0.0")
                implementation("dev.gitlive:firebase-auth:2.0.0")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation("org.jetbrains.compose.material3:material3-desktop:1.5.12")
                implementation("org.apache.poi:poi:5.2.3")
                implementation("org.apache.poi:poi-ooxml:5.2.3")
                implementation("io.ktor:ktor-client-core:1.6.4")
                implementation("io.ktor:ktor-client-java:1.6.4")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
                implementation("me.tatarka.inject:kotlin-inject-runtime-kmp:0.7.2")
                implementation("dev.gitlive:firebase-java-sdk:0.6.1")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.8.0")
            }
        }
        val jvmTest by getting
    }
}

dependencies {
    add("kspJvm", "me.tatarka.inject:kotlin-inject-compiler-ksp:0.7.2")
    add("kspJvm", "androidx.room:room-compiler:2.7.0-alpha01")
}

compose.desktop {
    application {
        mainClass = "jatx.expense.manager.MainKt"
        nativeDistributions {
            packageName = "ExpenseManager"
            packageVersion = "1.0.0"
        }
    }
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "jatx.expense.manager.MainKt"
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}
