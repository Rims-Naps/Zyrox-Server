rootProject.name = "zenyte-game-server-new"

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven(url = "https://jitpack.io")
        flatDir {
            dirs("lib")
        }
    }
    pluginManagement.plugins.apply {
        kotlin("jvm").version("1.8.22")
    }
}
