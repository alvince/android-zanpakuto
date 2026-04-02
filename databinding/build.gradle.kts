val modeOffline = providers.gradleProperty("release.offline").orNull?.toBoolean() ?: false

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
}

android {
    namespace = "cn.alvince.zanpakuto.databinding"
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
    compileOnly(libs.androidx.databinding.runtime)
    compileOnly(libs.androidx.databinding.adapters)

    implementation(libs.kotlin.stdlib)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.fragment.ktx)

    if (modeOffline) {
        api(project(":viewbinding"))
        implementation(project(":view"))
    } else {
        api("cn.alvince.zanpakuto:viewbinding:0.1-SNAPSHOT")
        implementation("cn.alvince.zanpakuto:view:0.1-SNAPSHOT")
    }

    testImplementation(libs.junit4)
}
