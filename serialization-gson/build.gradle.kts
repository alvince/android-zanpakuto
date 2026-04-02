val modeOffline = providers.gradleProperty("release.offline").orNull?.toBoolean() ?: false

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "cn.alvince.zanpakuto.serialization.gson"
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
}

dependencies {
    compileOnly(libs.google.gson)

    implementation(libs.kotlin.stdlib)

    if (modeOffline) {
        implementation(project(":core"))
    } else {
        implementation("cn.alvince.zanpakuto:core:1.0.1")
    }

    testImplementation(libs.junit4)
}
