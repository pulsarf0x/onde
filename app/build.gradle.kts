plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
}

android {
    namespace = "app.onde.audio"
    compileSdk = 36

    defaultConfig {
        applicationId = "app.onde.audio"
        minSdk = 30  // Android 11+ required for MediaRouter2 (AudioPlaybackCapture needs 10+)
        targetSdk = 36
        versionCode = 2
        versionName = "0.2.0"

        ndk {
            abiFilters += listOf("arm64-v8a", "armeabi-v7a", "x86_64")
        }
        externalNativeBuild {
            cmake {
                cppFlags += "-std=c++17"
            }
        }
    }

    // AGP 8.10 packages uncompressed libraries with 16 KiB alignment.
    packaging {
        jniLibs.useLegacyPackaging = false
    }

    ndkVersion = "27.2.12479018"

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }

    val uploadStore = providers.environmentVariable("ONDE_UPLOAD_STORE_FILE").orNull
    val uploadPassword = providers.environmentVariable("ONDE_UPLOAD_STORE_PASSWORD").orNull
    val uploadAlias = providers.environmentVariable("ONDE_UPLOAD_KEY_ALIAS").orNull
    val uploadKeyPassword = providers.environmentVariable("ONDE_UPLOAD_KEY_PASSWORD").orNull
    val uploadValues = listOf(uploadStore, uploadPassword, uploadAlias, uploadKeyPassword)
    val hasUploadSigning = uploadValues.all { !it.isNullOrBlank() }
    check(uploadValues.none { !it.isNullOrBlank() } || hasUploadSigning) {
        "Release signing requires all four ONDE_UPLOAD_* environment variables."
    }

    signingConfigs {
        if (hasUploadSigning) {
            create("upload") {
                storeFile = rootProject.file(uploadStore!!)
                storePassword = uploadPassword
                keyAlias = uploadAlias
                keyPassword = uploadKeyPassword
            }
        }
        getByName("debug") {
            providers.gradleProperty("ondeDebugKeystore").orNull?.let { storeFile = file(it) }
        }
    }

    buildTypes {
        debug {
            isDebuggable = true
            buildConfigField("boolean", "ENABLE_SHIZUKU", "true")
        }
        release {
            isDebuggable = false
            buildConfigField("boolean", "ENABLE_SHIZUKU", "false")
            if (hasUploadSigning) signingConfig = signingConfigs.getByName("upload")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
        aidl = true
    }
}

dependencies {
    // AndroidX Core
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.activity:activity-ktx:1.8.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    
    // MediaStyle notifications
    implementation("androidx.media:media:1.7.0")

    // AndroidX MediaRouter (required to fix Apple Music crash by providing proper MediaRoute2Info extras)
    implementation("androidx.mediarouter:mediarouter:1.6.0")

    // jmDNS for AirPlay discovery (mDNS/Bonjour)
    implementation("org.jmdns:jmdns:3.5.9")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    
    // RecyclerView for speaker list
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    
    // Bouncy Castle for SRP-6a crypto (AirPlay 2 Pairing)
    implementation("org.bouncycastle:bcprov-jdk15on:1.70")

    // dd-plist for AirPlay 2 binary plist (bplist00) RTSP bodies
    implementation("com.googlecode.plist:dd-plist:1.28")

    // Shizuku: optional privileged helper (skip consent dialogs, silent-phone capture)
    debugImplementation("dev.rikka.shizuku:api:13.1.5")
    debugImplementation("dev.rikka.shizuku:provider:13.1.5")

    // Unit testing
    testImplementation("junit:junit:4.13.2")
}
