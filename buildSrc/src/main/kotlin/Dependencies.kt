object Versions {
    const val kotlin = "1.9.21"
    const val agp = "8.2.2"
    const val compose = "1.5.8"
    const val composeBom = "2024.02.00"
    const val hilt = "2.48.1"
    const val lifecycle = "2.7.0"
    const val navigation = "2.7.6"
    const val activity = "1.8.2"
    const val coreKtx = "1.12.0"
    const val retrofit = "2.9.0"
    const val okhttp = "4.12.0"
    const val detekt = "1.23.4"
    const val ktlint = "12.1.0"
    const val spotless = "6.25.0"
}

object Dependencies {
    // Android Core
    const val coreKtx = "androidx.core:core-ktx:${Versions.coreKtx}"
    const val lifecycleRuntimeKtx = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycle}"
    const val lifecycleViewModelCompose = "androidx.lifecycle:lifecycle-viewmodel-compose:${Versions.lifecycle}"
    const val activityCompose = "androidx.activity:activity-compose:${Versions.activity}"
    
    // Compose BOM
    const val composeBom = "androidx.compose:compose-bom:${Versions.composeBom}"
    
    // Compose UI
    const val composeUi = "androidx.compose.ui:ui"
    const val composeUiGraphics = "androidx.compose.ui:ui-graphics"
    const val composeUiToolingPreview = "androidx.compose.ui:ui-tooling-preview"
    const val composeUiTooling = "androidx.compose.ui:ui-tooling"
    const val composeUiTestManifest = "androidx.compose.ui:ui-test-manifest"
    const val composeUiTestJunit4 = "androidx.compose.ui:ui-test-junit4"
    
    // Material Design
    const val material3 = "androidx.compose.material3:material3"
    const val materialIconsExtended = "androidx.compose.material:material-icons-extended"
    
    // Navigation
    const val navigationCompose = "androidx.navigation:navigation-compose:${Versions.navigation}"
    
    // Dependency Injection
    const val hiltAndroid = "com.google.dagger:hilt-android:${Versions.hilt}"
    const val hiltCompiler = "com.google.dagger:hilt-android-compiler:${Versions.hilt}"
    const val hiltNavigationCompose = "androidx.hilt:hilt-navigation-compose:1.1.0"
    const val hiltAndroidTesting = "androidx.hilt:hilt-android-testing:${Versions.hilt}"
    
    // Networking
    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    const val retrofitConverterGson = "com.squareup.retrofit2:converter-gson:${Versions.retrofit}"
    const val okhttpLoggingInterceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.okhttp}"
    
    // Testing
    const val junit = "junit:junit:4.13.2"
    const val kotlinxCoroutinesTest = "org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3"
    const val truth = "com.google.truth:truth:1.1.4"
    const val mockk = "io.mockk:mockk:1.13.8"
    const val androidTestJunit = "androidx.test.ext:junit:1.1.5"
    const val espressoCore = "androidx.test.espresso:espresso-core:3.5.1"
}

object Plugins {
    const val androidApplication = "com.android.application"
    const val androidLibrary = "com.android.library"
    const val kotlinAndroid = "kotlin-android"
    const val kotlinKapt = "kotlin-kapt"
    const val hiltAndroid = "dagger.hilt.android.plugin"
    const val detekt = "io.gitlab.arturbosch.detekt"
    const val ktlint = "org.jlleitschuh.gradle.ktlint"
    const val spotless = "com.diffplug.spotless"
}

object AndroidConfig {
    const val compileSdk = 34
    const val minSdk = 24
    const val targetSdk = 34
    const val versionCode = 1
    const val versionName = "1.0"
    const val applicationId = "com.riggerconnect"
    const val testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
}
