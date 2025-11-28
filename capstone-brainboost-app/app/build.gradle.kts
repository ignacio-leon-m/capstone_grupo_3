plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt")
}

android {
    namespace = "com.bboost.brainboost"
    compileSdk = 36   // <<<<<< CORREGIDO

    defaultConfig {
        applicationId = "com.bboost.brainboost"
        minSdk = 24
        targetSdk = 36   // <<<<<< CORREGIDO
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
        languageVersion = "2.0"
        apiVersion = "2.0"
    }

    buildFeatures {
        compose = true
        viewBinding = true
    }
}

dependencies {

    // ---------- COMPOSE BOM ----------
    implementation(platform(libs.androidx.compose.bom))

    // ---------- COMPOSE ----------
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // Íconos extendidos (para UploadFile, School, Logout, etc.)
    implementation("androidx.compose.material:material-icons-extended")

    // ---------- CORE / LIFECYCLE ----------
    implementation(libs.androidx.core.ktx)   // ahora usa la versión correcta para compileSdk 36
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.3")

    // ---------- RETROFIT / OKHTTP ----------
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okio:okio:3.9.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // ---------- COROUTINES / SERIALIZATION ----------
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.7.3")

    // ---------- ANDROID UI ----------
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // ---------- TESTING ----------
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
