rootProject.name = "AniFlow"

include(":composeApp", ":composeApp:sekret")
include(":firebase")
include(":anilist")
include(":model")

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://jitpack.io")
        maven("https://jogamp.org/deployment/maven")
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}
