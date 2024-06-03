// Top-level build file where you can add configuration options common to all sub-projects/modules.
/*
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(libs.gradle)
    }
}
*/

/*allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url  = uri("https://jitpack.io") }
    }
}*/

plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    id("androidx.navigation.safeargs")  version "2.7.7" apply false
}
