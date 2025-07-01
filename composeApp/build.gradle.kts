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
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
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
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation("org.usb4java:usb4java:1.3.0")
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.jetbrains.compose.navigation)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.androidx.room.runtime)
            implementation(libs.sqlite.bundled)
            implementation(libs.koin.compose)

            api(libs.koin.core)

            implementation(libs.bundles.ktor)
            implementation(libs.bundles.coil)
            implementation("org.jsoup:jsoup:1.15.3")
            implementation("org.apache.commons:commons-text:1.10.0")
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
            implementation(libs.ktor.client.okhttp)
        }
        dependencies {
            ksp(libs.androidx.room.compiler)
        }
    }
}

android {
    namespace = "com.ikemba.inventrar"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.ikemba.inventrar"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    //implementation(project(":composeApp"))
    debugImplementation(compose.uiTooling)
}




// ... other configurations ...

compose.desktop {
    application {
        mainClass = "com.ikemba.inventrar.MainKt"

        // Set the application display name here
      //  name = "Inventrar" // This is the correct property for the application's human-readable name

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Inventrar" // This is the unique identifier for your application
            packageVersion = "1.0.0"


            // Vendor information
            vendor = "Ikemba"

            // Application description
            description = "An inventory management application."

            // --- Windows specific configurations for MSI ---
            windows {
                // Set the application icon (.ico file)
                // Place icon.ico in, for example, 'src/main/resources/icon.ico'
                // Ensure this path is correct relative to your project root.
                iconFile.set(project.file("src/commonMain/resources/inventra_logo_colored.ico"))

                // To create a desktop shortcut
                // The shortcut will typically use the 'application.name'
                shortcut = true

                // To add an entry to the Start Menu
                menu = true
                // The menu entry will also typically use 'application.name'
                menuGroup = "Inventrar Applications" // Optional: specify a menu group

                // For Windows, the 'application.name' is generally used as the product name.
                // If you need a very specific name for the MSI package itself or in "Add/Remove Programs"
                // that differs from 'application.name', ensure 'packageName' is appropriate or check if
                // there are specific overrides, though 'application.name' is the primary source.

                // You might need to specify upgrade UUID if you plan to release updates
                // upgradeUuid = "YOUR_UNIQUE_GUID_HERE" // Generate a new GUID for your app
            }

            // --- macOS specific configurations (DMG) ---
            macOS {
                // Set the application icon (.icns file)
                // Place icon.icns in, for example, 'src/main/resources/icon.icns'
                // iconFile.set(project.file("src/main/resources/icon.icns"))

                // The application name on macOS will be taken from 'application.name'
                // and often the .app bundle is named accordingly.
                // bundleID = "com.ikemba.inventrar" // Usually matches nativeDistributions.packageName
            }

            // --- Linux specific configurations (Deb) ---
            linux {
                // Set the application icon (.png file)
                // Place icon.png in, for example, 'src/main/resources/icon.png'
                // iconFile.set(project.file("src/main/resources/icon.png"))

                // The application name on Linux will also be taken from 'application.name'.
                // appCategory = "Utility"
            }
        }
        buildTypes.release {
            proguard {
                isEnabled.set(false)  // Disable ProGuard
                // If you were to enable ProGuard, you would add rules here:
                // configurationFiles.from(project.file("proguard-rules.pro"))
            }
        }
    }
    // The problematic line "name = "Inventrar"" has been removed from here.
}

