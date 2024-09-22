plugins {
    alias(libs.plugins.android.application)
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.droid.itunesandroid"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.droid.itunesandroid"
        minSdk = 24
        targetSdk = 35
        versionCode = 2
        versionName = "2.0"

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
//noinspection KaptUsageInsteadOfKsp
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.swiperefreshlayout)

    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.runtime)

    kapt(libs.androidx.room.compiler)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.hilt.android.v2511)
    kapt(libs.hilt.android.compiler.v2511)
    implementation(libs.material.v150)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.glide)
    kapt(libs.compiler)

    //UnitTest
    testImplementation (libs.junit)
    testImplementation (libs.mockito.core)
    testImplementation (libs.mockito.mockito.inline)
    testImplementation (libs.kotlinx.coroutines.test)
    androidTestImplementation (libs.androidx.room.testing)
    testImplementation (libs.androidx.core.testing)
    testImplementation (libs.robolectric)


}

// Allow references to generated code
kapt {
    correctErrorTypes = true
}