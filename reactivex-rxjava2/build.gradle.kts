val modeOffline = providers.gradleProperty("release.offline").orNull?.toBoolean() ?: false

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "cn.alvince.zanpakuto.rxjava2"
    compileSdk = 31

    defaultConfig {
        minSdk = 19
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    lint {
        abortOnError = false
    }
}

dependencies {
    api(libs.rxjava.core)
    api(libs.rxjava.kotlin)
    api(libs.rxjava.android)

    implementation(libs.kotlin.stdlib)
    implementation(libs.androidx.core)
    implementation(libs.androidx.collection)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.fragment)

    if (modeOffline) {
        implementation(project(":lifecycle"))
    } else {
        implementation("cn.alvince.zanpakuto:lifecycle:1.0.0-SNAPSHOT")
    }

    testImplementation(libs.junit4)
}
