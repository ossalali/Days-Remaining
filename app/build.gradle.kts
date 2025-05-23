plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.compose.compiler)
    kotlin("plugin.serialization") version "1.9.22" // Added Kotlin serialization plugin
}

android {
    namespace = "com.ossalali.daysremaining"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.ossalali.daysremaining"
        minSdk = 29
        targetSdk = 35
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    @Suppress("UnstableApiUsage")
    composeOptions {
        // Use the version that matches your Compose runtime libraries.
        kotlinCompilerExtensionVersion = "1.5.11"
    }

    packaging {
        resources {
            // Exclude the duplicate incremental annotation processors file.
            excludes += "META-INF/gradle/incremental.annotation.processors"
        }
    }
}

dependencies {
    // Hilt dependencies:
    implementation(libs.hilt.android)
    implementation(libs.androidx.room.common)
    implementation(libs.androidx.room.ktx)
    testImplementation(libs.junit.jupiter)
    ksp(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)


    // Compose and other dependencies:
    ksp(libs.androidx.room.compiler)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.runtime.android)
    implementation(libs.androidx.foundation.layout.android)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.material)
    implementation(libs.androidx.material3.v140alpha07)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.accompanist.navigation.animation)
    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.ui)

    debugImplementation(libs.androidx.ui.tooling)

    testImplementation(libs.junit)
    // JUnit 4, if specifically needed alongside JUnit 5 (libs.junit.jupiter)
    // testImplementation("junit:junit:4.13.2") 
    // testImplementation("androidx.test.ext:junit:1.1.5") // For AndroidX Test extensions for JUnit

    // Room testing
    testImplementation("androidx.room:room-testing:2.6.1")

    // Coroutines testing
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")

    // Mockito for testing
    testImplementation("org.mockito:mockito-core:5.11.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")


    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // DataStore and Kotlinx Serialization
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
}