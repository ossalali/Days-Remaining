plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
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
    }

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
    // Hilt runtime
    implementation(libs.hilt.android)
    implementation(libs.androidx.room.common)
    implementation(libs.androidx.room.ktx)
    // Hilt compiler (annotation processor)
    ksp(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // Explicitly add Room compiler to supply the missing class
    ksp(libs.androidx.room.compiler)

    // Compose and other dependencies:
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
    implementation (libs.androidx.material.icons.extended)

    debugImplementation(libs.androidx.ui.tooling)

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}