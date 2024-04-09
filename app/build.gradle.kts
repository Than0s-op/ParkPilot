plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.application.parkpilot"
    compileSdk = 34

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.application.parkpilot"
        minSdk = 28
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-auth:22.3.1")
    implementation("androidx.leanback:leanback:1.0.0")
    implementation("com.google.firebase:firebase-firestore:24.10.1")
    implementation("com.google.firebase:firebase-storage:20.3.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")


    /* Add Third party dependencies here. (don't forget to drop the comment) */

    // country code picker
    implementation("com.hbb20:ccp:2.7.0")

    // OTP Box view
    implementation("io.github.chaosleung:pinview:1.4.4")

    // openStreetMap
    implementation("org.osmdroid:osmdroid-android:6.1.18")

    // add the dependency for the Google Play services library and specify its version (google sign in)
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))

    // kotlin coroutine library
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // fused location provider to get last known location
    implementation("com.google.android.gms:play-services-location:21.1.0")

    // image caching and downloading (Co-routine image loader)
    implementation("io.coil-kt:coil:2.5.0")

    // view model dependency
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")

    // live data dependency
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")

    // razor pay
    implementation("com.razorpay:checkout:1.6.36")

    // QR generator (https://github.com/alexzhirkevich/custom-qr-generator?tab=readme-ov-file)
    implementation("com.github.alexzhirkevich:custom-qr-generator:1.6.2")

    // flex-box
    implementation("com.google.android.flexbox:flexbox:3.0.0")

    // avatar image generator
    implementation("com.github.amoskorir:avatarimagegenerator:1.5.0")

    // refresh layout
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0-alpha01")
}