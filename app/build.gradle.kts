plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.pulse.widgets"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.pulse.widgets" // unchanged, so this installs as an update
        minSdk = 29
        targetSdk = 34
        // grows with every GitHub Actions run, so each new APK is always an upgrade
        versionCode = 100 + (System.getenv("GITHUB_RUN_NUMBER")?.toIntOrNull() ?: 0)
        versionName = "2.0"
    }

    // One fixed key for every build. Android only allows an in-place update when the signature matches,
    // and GitHub runners otherwise create a brand new random debug key each time.
    signingConfigs {
        create("pulse") {
            storeFile = file("pulse.keystore")
            storePassword = "pulsewidgets"
            keyAlias = "pulse"
            keyPassword = "pulsewidgets"
        }
    }

    buildTypes {
        getByName("debug") { signingConfig = signingConfigs.getByName("pulse") }
        getByName("release") {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("pulse")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}
