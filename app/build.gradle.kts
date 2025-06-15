plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp")
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.roblocks"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.roblocks"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        debug {
            buildConfigField("String", "BASE_URL", "\"https://roblocks.pythonanywhere.com\"")
            buildConfigField("String", "CLASSIFIER_URL", "\"http://10.10.193.199:5000\"")

        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "BASE_URL", "\"https://roblocks.pythonanywhere.com\"")
            buildConfigField("String", "CLASSIFIER_URL", "\"http://10.10.193.199:5000\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}


dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.androidx.navigation.runtime.android)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.navigation.compose)
    implementation ("androidx.compose.material3:material3:1.2.0-alpha03")
    // Room dependencies
    implementation(libs.androidx.room.ktx)
    ksp("androidx.room:room-compiler:2.5.0")

    // UI
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    //firebase
    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation ("androidx.activity:activity-compose:1.9.0")


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation("androidx.compose.material:material-icons-extended")

    // WebView
    implementation("androidx.webkit:webkit:1.8.0")


    implementation("com.pierfrancescosoffritti.androidyoutubeplayer:core:12.1.0")

    implementation(libs.androidx.webkit.v1130) // Make sure you include the WebView dependency
    implementation("androidx.compose.ui:ui:1.8.0") // Make sure you have the latest Compose version
    implementation(libs.material3) // For Material 3
    implementation("androidx.navigation:navigation-compose:2.5.0") // For navigation support


    // TensorFlow Lite

//    implementation (libs.tensorflow.lite)
//    implementation (libs.tensorflow.lite.support)
//    implementation (libs.tensorflow.lite.metadata)
//    implementation (libs.tensorflow.lite.task.vision)

    implementation ("org.tensorflow:tensorflow-lite-task-vision:0.4.4")
    implementation ("org.tensorflow:tensorflow-lite-support:0.4.4")
    implementation ("org.tensorflow:tensorflow-lite:2.16.1")


    // Coroutines
    implementation (libs.kotlinx.coroutines.android)
    implementation (libs.androidx.lifecycle.runtime.ktx.v262)
    implementation (libs.androidx.lifecycle.viewmodel.compose)

    // File operations
    implementation (libs.commons.io)
    // Image loading
    implementation (libs.coil.compose)
    implementation(libs.okhttp)
    implementation (libs.kotlinx.coroutines.android.v173)

    implementation (libs.gson)

    //dagger-hilt
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-android-compiler:2.51.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")


    //retrofit
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    //chart
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // Google Sign In
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.google.firebase:firebase-auth-ktx")

    implementation ("com.google.firebase:firebase-auth:22.3.1")
    implementation ("com.google.android.gms:play-services-auth:20.7.0")

    implementation ("androidx.credentials:credentials:<latest version>")
    implementation ("androidx.credentials:credentials-play-services-auth:<latest version>")
    implementation ("com.google.android.libraries.identity.googleid:googleid:<latest version>")

}

kapt {
    correctErrorTypes = true
}
