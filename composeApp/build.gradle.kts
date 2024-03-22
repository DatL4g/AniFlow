import com.mikepenz.aboutlibraries.plugin.DuplicateMode
import com.mikepenz.aboutlibraries.plugin.DuplicateRule
import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.aboutlibraries)
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose)
    alias(libs.plugins.moko.resources)
    alias(libs.plugins.sekret)
    alias(libs.plugins.serialization)
    alias(libs.plugins.ktorfit)
}

val artifact = VersionCatalog.artifactName()

group = artifact
version = appVersion

multiplatformResources {
    resourcesPackage.set(artifact)
    resourcesClassName.set("SharedRes")
}

aboutLibraries {
    includePlatform = true
    duplicationMode = DuplicateMode.MERGE
    duplicationRule = DuplicateRule.GROUP
    excludeFields = arrayOf("generated")
}

sekret {
    properties {
        enabled.set(true)
        packageName.set(artifact)

        androidJNIFolder.set(project.layout.projectDirectory.dir("src/androidMain/jniLibs"))
    }
}

kotlin {
    androidTarget()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)

            api(libs.decompose)
            implementation(libs.decompose.compose)
            implementation(libs.coroutines)
            implementation(libs.kodein)
            implementation(libs.kodein.compose)

            implementation(libs.tooling.decompose)
            implementation(libs.napier)
            implementation(libs.moko.resources.compose)

            implementation(libs.windowsize)

            implementation(libs.coil)
            implementation(libs.coil.network)
            implementation(libs.coil.svg)
            implementation(libs.coil.compose)

            implementation(libs.kmpalette)
            implementation(libs.kolor)

            implementation(libs.haze)
            implementation(libs.haze.materials)

            implementation(libs.datetime)
            implementation(libs.ktor.content.negotiation)
            implementation(libs.ktor.serialization.json)

            implementation(libs.aboutlibraries)
            implementation(libs.kasechange)

            implementation(project(":firebase"))
            implementation(project(":anilist"))
            implementation(project(":model"))
        }

        iosMain.dependencies {
            implementation(libs.ktor.darwin)
        }

        val androidMain by getting {
            apply(plugin = "kotlin-parcelize")
            apply(plugin = libs.plugins.crashlytics.get().pluginId)

            dependencies {
                implementation(libs.android)
                implementation(libs.activity)
                implementation(libs.activity.compose)
                implementation(libs.appcompat)
                implementation(libs.multidex)
                implementation(libs.splashscreen)
                implementation(libs.html.converter)

                implementation(libs.ktor.jvm)
                implementation(libs.coroutines.android)

                implementation(libs.android.credentials.play.services)
            }
        }
    }
}

android {
    sourceSets["main"].setRoot("src/androidMain/")
    sourceSets["main"].res.srcDirs("src/androidMain/res", "src/commonMain/resources")
    sourceSets["main"].assets.srcDirs("src/androidMain/assets", "src/commonMain/assets")
    compileSdk = Configuration.compileSdk
    namespace = artifact

    defaultConfig {
        applicationId = artifact
        minSdk = Configuration.minSdk
        targetSdk = Configuration.targetSdk
        versionCode = appVersionCode
        versionName = appVersion

        multiDexEnabled = true
        vectorDrawables.useSupportLibrary = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    compileOptions {
        sourceCompatibility = CompileOptions.sourceCompatibility
        targetCompatibility = CompileOptions.targetCompatibility
    }
    buildFeatures {
        buildConfig = true
    }
}
