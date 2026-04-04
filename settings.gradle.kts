pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }

    plugins {
        kotlin("multiplatform").version(extra["kotlin.version"] as String).apply(false)
        id("org.jetbrains.kotlin.plugin.compose").version(extra["kotlin.version"] as String).apply(false)
        id("org.jetbrains.compose").version(extra["compose.version"] as String).apply(false)
        id("androidx.room").version(extra["room.version"] as String).apply(false)
        id("com.android.library").version(extra["agp.version"] as String).apply(false)
        id("com.android.application") version "8.12.0"
        id("org.jetbrains.kotlin.android") version "2.0.21"
    }
}

rootProject.name = "ExpenseManager"

include(":androidApp")
