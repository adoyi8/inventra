plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false



    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.room) apply false
    id("com.google.gms.google-services") version "4.4.3" apply false
}
buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://jitpack.io")             // ← JitPack :contentReference[oaicite:0]{index=0}
    }
    dependencies {
        classpath("com.google.gms:google-services:4.4.1")
    }
}