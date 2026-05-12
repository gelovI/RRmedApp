import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.jetbrains.kotlin.plugin.compose")
}

val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use(::load)
    }
}

fun signingProperty(name: String): String? =
    localProperties.getProperty(name) ?: System.getenv(name)

val releaseStoreFile = signingProperty("RRMED_RELEASE_STORE_FILE")
val releaseStorePassword = signingProperty("RRMED_RELEASE_STORE_PASSWORD")
val releaseKeyAlias = signingProperty("RRMED_RELEASE_KEY_ALIAS")
val releaseKeyPassword = signingProperty("RRMED_RELEASE_KEY_PASSWORD")

val hasReleaseSigning = listOf(
    releaseStoreFile,
    releaseStorePassword,
    releaseKeyAlias,
    releaseKeyPassword
).all { !it.isNullOrBlank() }


android {
    namespace = "com.example.bloodpressureapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.bloodpressureapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 12
        versionName = "1.11"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        if (hasReleaseSigning) {
            create("release") {
                storeFile = file(releaseStoreFile!!)
                storePassword = releaseStorePassword
                keyAlias = releaseKeyAlias
                keyPassword = releaseKeyPassword
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")

            if (hasReleaseSigning) {
                signingConfig = signingConfigs.getByName("release")
            }
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
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.ui.util.android)
    val composeBom = platform("androidx.compose:compose-bom:2023.10.01")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // Compose basics
    implementation(libs.androidx.compose.ui.ui2)
    implementation(libs.androidx.compose.material.material)
    implementation(libs.androidx.compose.ui.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.ui.tooling)

    // Compose UI Tests (Instrumented!)
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.test.manifest)

    // Activity Compose
    implementation(libs.activity.compose.v180)

    // Room
    implementation(libs.androidx.room.runtime.v261)
    implementation(libs.room.ktx.v261)
    ksp(libs.androidx.room.compiler.v261)

    // Lifecycle
    implementation(libs.lifecycle.runtime.ktx.v262)
    implementation(libs.lifecycle.viewmodel.compose.v262)

    // Coroutines
    implementation(libs.kotlinx.coroutines.core.v190)
    implementation(libs.kotlinx.coroutines.android.v190)

    implementation(libs.mpandroidchart)
    implementation(libs.material.icons.extended)
    implementation(libs.material.v1110)
    implementation(libs.kotlinx.serialization.json.v162)

    // Android Test Basics
    androidTestImplementation(libs.androidx.junit.v115)
    androidTestImplementation(libs.androidx.espresso.core.v351)

    // Unit Test
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)

}