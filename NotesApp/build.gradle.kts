buildscript {
    extra.apply {
        set(
            "room_version",
            "2.7.1" // проверить новую версию можно тут: https://developer.android.com/jetpack/androidx/releases/room
        )
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false

    kotlin("jvm") version "2.1.10" apply false
    id("com.google.devtools.ksp") version "2.1.10-1.0.31" apply false
    alias(libs.plugins.android.library) apply false
}