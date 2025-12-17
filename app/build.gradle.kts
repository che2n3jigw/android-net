import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlinx.serialization)
}

android {
    namespace = "com.che2n3jigw.android.net"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.che2n3jigw.android.net"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
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
    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_1_8
        }
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    // 自定义retrofit封装库
    implementation(libs.android.net)
    // implementation(project(":lib_net"))
    // 协程
    implementation(libs.kotlinx.coroutines.core)
    // retrofit
    implementation(libs.retrofit)
    // 实体类转换器
    implementation(libs.converter.kotlinx.serialization)
    implementation(libs.kotlinx.serialization.json)
}