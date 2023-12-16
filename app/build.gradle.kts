@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    id ("kotlin-parcelize")

}

android {
    namespace = "com.example.m5"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.m5"
        minSdk = 24
        targetSdk = 33
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

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)

    //Glide for image loading
    implementation(libs.glide)
    annotationProcessor(libs.compiler)

    //for Notification,导入media
    implementation(libs.androidx.media)

    //for storing
    implementation(libs.gson)

    //for sending email
    implementation(libs.android.mail)
    implementation(libs.android.activation)

    //亚克力模糊
//    implementation (libs.blurimageviewlib)

    implementation (libs.blurkit)

    //用于上滑动画

    implementation (libs.androidx.viewpager2)
    implementation (libs.material.v140)



    //for sending email
    implementation(libs.android.mail)
    implementation(libs.android.activation)
    implementation ("androidx.recyclerview:recyclerview:1.0.0")
    implementation ("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.2.0")
    implementation ("com.google.android.material:material:1.1.0")
    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.0.0")
    implementation ("com.squareup.retrofit2:retrofit:2.6.1")
    implementation ("com.squareup.retrofit2:converter-gson:2.6.1")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.1.1")

    implementation (libs.androidx.room.runtime)


}