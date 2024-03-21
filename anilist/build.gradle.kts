plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.apollo)
    alias(libs.plugins.serialization)
}

val artifact = VersionCatalog.artifactName("anilist")

apollo {
    service("AniList") {
        packageName.set(artifact)
        srcDir("src/commonMain/graphql")
        introspection {
            endpointUrl.set("https://graphql.anilist.co/")
            schemaFile.set(file("src/commonMain/graphql/schema.graphqls"))
        }
    }
}

kotlin {
    androidTarget()

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain.dependencies {
            api(libs.apollo)
            implementation(libs.kache)
            api(libs.flowredux)
            implementation(libs.datetime)
            implementation(libs.serialization)

            implementation(project(":model"))
            implementation(project(":firebase"))
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
