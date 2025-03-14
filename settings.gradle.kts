plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "android-build-flavorize"
