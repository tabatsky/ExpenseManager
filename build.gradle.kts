import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("com.squareup.sqldelight")
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
        val commonMain by getting
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.0")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.7.1")
                implementation("org.apache.poi:poi:5.2.3")
                implementation("org.apache.poi:poi-ooxml:5.2.3")
                implementation("com.squareup.sqldelight:sqlite-driver:1.5.5")
            }
        }
        val jvmTest by getting
    }
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
    }
}
