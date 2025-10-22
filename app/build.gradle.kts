plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
    id("com.google.gms.google-services")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.mobile.app_iara"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.mobile.app_iara"
        minSdk = 33
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
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.activity)
    implementation("com.google.android.material:material:1.12.0")

    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    kapt("com.github.bumptech.glide:compiler:4.16.0")

    // Autenticação e Credenciais
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    kapt ("androidx.room:room-compiler:2.6.1")
    implementation ("androidx.room:room-ktx:2.6.1")

    // Work Runtime
    implementation ("androidx.work:work-runtime-ktx:2.8.1")
    
    // Dependência para o Gráfico (MPAndroidChart)
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // Biblioteca para animação
    implementation("com.airbnb.android:lottie:6.4.1")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
