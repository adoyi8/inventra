import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    id("com.google.gms.google-services")
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
// ...


    jvm("desktop")

    room {
        schemaDirectory("$projectDir/schemas")
    }
    sourceSets {
        val desktopMain by getting

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)

            implementation(libs.koin.android)
            implementation(libs.koin.androidx.compose)
            implementation(libs.ktor.client.okhttp)

            // Android Credential Manager & Auth
            implementation(platform("com.google.firebase:firebase-bom:33.7.0")) // Updated BOM
            implementation("com.google.android.gms:play-services-auth:21.3.0")
            implementation("com.google.android.gms:play-services-base:18.5.0")
            implementation("androidx.credentials:credentials:1.3.0")
            implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")
        }
        commonMain.dependencies {
            // FIX 1: Use the Compose BOM to enforce consistent versions for all Compose dependencies
            implementation(libs.androidx.compose.bom)

            implementation(compose.runtime)
            implementation(compose.foundation)

            // Material 3 Expressive (The reason we needed SDK 35)
            implementation("org.jetbrains.compose.material3:material3:1.9.0-alpha04")

            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)

            implementation("org.usb4java:usb4java:1.3.0")
            implementation(libs.jetbrains.compose.navigation)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.androidx.room.runtime)
            implementation(libs.sqlite.bundled)
            implementation(libs.koin.compose)

            api(libs.koin.core)

            implementation(libs.bundles.ktor)
            implementation(libs.bundles.coil)
            implementation("org.jsoup:jsoup:1.18.3") // Minor update for security
            implementation("org.apache.commons:commons-text:1.12.0") // Minor update

            // FIX 2: Upgraded Multiplatform Firebase Auth
            implementation("dev.gitlive:firebase-auth:2.3.0")
            implementation("dev.gitlive:firebase-firestore:2.3.0")
            implementation(compose.materialIconsExtended)
            implementation(libs.connectivity.kmp)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
            implementation(libs.ktor.client.okhttp)
            implementation("com.github.librepdf:openpdf:1.3.30")
        }
        dependencies {
            ksp(libs.androidx.room.compiler)
        }
    }
}

android {
    namespace = "com.sundroid.kobokobo"

    // Reads 35 from libs.versions.toml
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.sundroid.kobokobo"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 5
        versionName = "1.0"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "com.sundroid.kobokobo.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Inventrar"
            packageVersion = "1.0.0"

            vendor = "Ikemba"
            description = "An inventory management application."

            windows {
                iconFile.set(project.file("src/commonMain/resources/inventra_logo_colored.ico"))
                shortcut = true
                menu = true
                menuGroup = "Inventrar Applications"
            }

            macOS {
                // bundleID = "com.sundroid.kobokobo"
            }

            linux {
                // appCategory = "Utility"
            }
        }
        buildTypes.release {
            proguard {
                isEnabled.set(false)
            }
        }
    }
}