val roomVersion = "2.8.4"

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
    id("androidx.room")
    kotlin("plugin.serialization") version "2.2.21"
    id("com.google.devtools.ksp") version "2.2.21-2.0.5"
    id("com.android.library")
}

group = "jatx.expense.manager"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

android {
    namespace = "jatx.expense.manager"
    compileSdk = 34

    defaultConfig {
        minSdk = 26
        targetSdk = 34
    }
}

kotlin {
    androidTarget()
    jvm()
    jvmToolchain(22)
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("androidx.room:room-runtime:$roomVersion")
                implementation("androidx.sqlite:sqlite-bundled:2.5.0-alpha01")
                implementation("androidx.sqlite:sqlite:2.5.0-alpha01")
                implementation("dev.gitlive:firebase-common:2.4.0")
                implementation("dev.gitlive:firebase-auth:2.4.0")
                implementation("dev.gitlive:firebase-firestore:2.4.0")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
                implementation("com.google.code.gson:gson:2.13.1")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
                implementation("io.ktor:ktor-client-core:2.3.12")
                implementation("org.apache.poi:poi:5.5.1")
                implementation("org.apache.poi:poi-ooxml:5.5.1")
                implementation("org.jetbrains.compose.ui:ui:1.10.3")
                implementation("org.jetbrains.compose.ui:ui-graphics:1.10.3")
                implementation("org.jetbrains.compose.material:material:1.10.3")
                implementation("org.jetbrains.compose.material3:material3:1.4.0")
                implementation("me.tatarka.inject:kotlin-inject-runtime-kmp:0.7.2")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation("org.jetbrains.compose.material3:material3-desktop:1.5.12")
                implementation("io.ktor:ktor-client-java:2.3.12")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.10.2")
                implementation("dev.gitlive:firebase-firestore-jvm:2.4.0")
                implementation("dev.gitlive:firebase-java-sdk:0.6.2")
                implementation("me.tatarka.inject:kotlin-inject-runtime-kmp:0.7.2")
            }
        }
        val jvmTest by getting
        val androidMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
                implementation("dev.gitlive:firebase-common-android:2.4.0")
                implementation("dev.gitlive:firebase-auth-android:2.4.0")
                implementation("dev.gitlive:firebase-firestore-android:2.4.0")
                implementation("io.ktor:ktor-client-android:2.3.12")
                implementation("androidx.core:core-ktx:1.18.0")
                implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.10.0")
                implementation(project.dependencies.platform("com.google.firebase:firebase-bom:33.8.0"))
                implementation("com.google.firebase:firebase-auth-ktx")
                implementation("com.google.firebase:firebase-firestore-ktx")
                implementation("com.google.firebase:firebase-common-ktx")
                implementation("me.tatarka.inject:kotlin-inject-runtime:0.7.2")
            }
        }

        all {
            languageSettings {
                // Suppress internal serialization API warnings
                optIn("kotlinx.serialization.InternalSerializationApi")
            }
        }
    }
}

dependencies {
    add("kspJvm", "me.tatarka.inject:kotlin-inject-compiler-ksp:0.7.2")
    add("kspAndroid", "me.tatarka.inject:kotlin-inject-compiler-ksp:0.7.2")
    add("kspJvm", "androidx.room:room-compiler:$roomVersion")
    add("kspAndroid", "androidx.room:room-compiler:$roomVersion")
}

compose.desktop {
    application {
        mainClass = "jatx.expense.manager.MainKt"
        nativeDistributions {
            packageName = "ExpenseManager"
            packageVersion = "1.0.0"
        }
        buildTypes.release {
            proguard {
                isEnabled.set(false)
            }
        }
    }
}

//tasks.withType<Jar> {
//    manifest {
//        attributes["Main-Class"] = "jatx.expense.manager.MainKt"
//    }
//}

room {
    schemaDirectory("$projectDir/schemas")
}
