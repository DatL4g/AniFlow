plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.sekret)
}

val artifact = VersionCatalog.artifactName("firebase")

sekret {
    properties {
        enabled.set(false)
    }
}

kotlin {
    jvm()
    androidTarget()

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    macosX64()
    macosArm64()

    linuxX64()
    linuxArm64()

    mingwX64()

    js(IR) {
        nodejs()
        browser()
        binaries.executable()
    }

    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.tooling)
            implementation(libs.sekret)
        }

        val firebaseMain by creating {
            dependsOn(commonMain.get())

            androidMain.orNull?.dependsOn(this)
            jvmMain.orNull?.dependsOn(this)
            iosMain.orNull?.dependsOn(this)
            jsMain.orNull?.dependsOn(this)

            dependencies {
                implementation(libs.firebase.auth)
            }
        }

        androidMain.dependencies {
            implementation(libs.firebase.android)
            implementation(libs.firebase.android.analytics)
            implementation(libs.firebase.android.auth)
            implementation(libs.firebase.android.crashlytics)

            implementation(libs.firebase.crashlytics)

            api(libs.android.credentials)
            api(libs.google.identity)
        }

        val iosMain by getting {
            dependencies {
                implementation(libs.firebase.crashlytics)
            }
        }
    }
}

android {
    compileSdk = Configuration.compileSdk
    namespace = artifact

    defaultConfig {
        minSdk = Configuration.minSdk
    }
    compileOptions {
        sourceCompatibility = CompileOptions.sourceCompatibility
        targetCompatibility = CompileOptions.targetCompatibility
    }
}