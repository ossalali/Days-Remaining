plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.ossalali.daysremaining"
    compileSdk = 36

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
              "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlin { compilerOptions { jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11 } }

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

    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.ui)

    // Lifecycle dependencies
    implementation(libs.androidx.lifecycle.process)

    // Nav3
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.kotlinx.serialization.core)

    // Material3 Adaptive - temporarily disabled due to library availability
    // implementation(libs.androidx.material3.adaptive.layout)
    // implementation(libs.androidx.material3.adaptive.navigation3)

    debugImplementation(libs.androidx.ui.tooling)

    testImplementation(libs.junit)

    // Room testing
    testImplementation(libs.androidx.room.testing)

    // Coroutines testing
    testImplementation(libs.kotlinx.coroutines.test)

    // Mockito for testing
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // DataStore and Kotlinx Serialization
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.kotlinx.serialization.json)
}
