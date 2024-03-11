import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin
import org.jetbrains.kotlin.gradle.targets.js.yarn.yarn
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.nio.file.Files

plugins {
    alias(libs.plugins.aboutlibraries) apply false
    alias(libs.plugins.android) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.cocoapods) apply false
    alias(libs.plugins.compose) apply false
    alias(libs.plugins.crashlytics) apply false
    alias(libs.plugins.multiplatform) apply false
    alias(libs.plugins.moko.resources) apply false
    alias(libs.plugins.sekret) apply false
    alias(libs.plugins.serialization) apply false
    alias(libs.plugins.complete.kotlin)
    alias(libs.plugins.versions)
}

buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://jitpack.io")
        maven("https://jogamp.org/deployment/maven")
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
    dependencies {
        classpath(libs.moko.resources.generator)
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://jitpack.io")
        maven("https://jogamp.org/deployment/maven")
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }

    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions.jvmTarget = CompileOptions.jvmTarget
    }
    plugins.withType<YarnPlugin> {
        yarn.yarnLockAutoReplace = true
    }
}