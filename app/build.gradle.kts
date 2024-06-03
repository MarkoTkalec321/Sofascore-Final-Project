@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("com.android.application")
    id("kotlin-android")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.devtools.ksp") version "1.9.23-1.0.20" apply true
    id("kotlin-kapt")
}

android {
    namespace = "com.sofascore.scoreandroidacademy"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.sofascore.scoreandroidacademy"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures {
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    kotlin {
        sourceSets {
            main {
                java {
                    kotlin.srcDir("build/generated/source/navigation-args")
                }
            }
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.paging.common.android)
    implementation(libs.androidx.paging.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.retrofit)
    implementation (libs.converter.gson)
    implementation (libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.room.ktx)
    implementation(libs.room.runtime)
    //implementation(libs.androidx.datastore.core.android)
    //implementation (libs.androidx.datastore.preferences.v100)

    kapt(libs.room.compiler)
    //ksp(libs.room.compiler) //ovo ne radi!!!
    implementation(libs.coil)
    //implementation(libs.androidx.paging)


}