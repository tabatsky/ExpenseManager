plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("com.squareup.sqldelight")
    kotlin("plugin.serialization") version "1.9.20"
    id("com.google.devtools.ksp") version "1.9.20-1.0.14"
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
        jvmToolchain(17)
        withJava()
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
            }
        }
        val jvmMain by getting {
            @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation("org.jetbrains.compose.material3:material3-desktop:1.5.12")
                implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.0")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.7.1")
                implementation("org.apache.poi:poi:5.2.3")
                implementation("org.apache.poi:poi-ooxml:5.2.3")
                implementation("com.squareup.sqldelight:sqlite-driver:1.5.5")
                implementation("io.ktor:ktor-client-core:1.6.4")
                implementation("io.ktor:ktor-client-java:1.6.4")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
                implementation("me.tatarka.inject:kotlin-inject-runtime-kmp:0.7.2")
            }
        }
        val jvmTest by getting
    }
}

dependencies {
    add("kspJvm", "me.tatarka.inject:kotlin-inject-compiler-ksp:0.7.2")
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

sqldelight {
    database("AppDatabase") {
        packageName = "jatx.expense.manager.db"
        dialect = "sqlite:3.18"
        schemaOutputDirectory = file("jatx.expense.manager.db")
        migrationOutputDirectory = file("migrations")
        deriveSchemaFromMigrations = true
        verifyMigrations = true
    }
}
