plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.atomicfu)
}

kotlin {
    jvm()

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
            implementation(libs.coroutines)
            implementation(libs.tooling)
            api(libs.immutable)
            api(libs.serialization)
        }
    }
}