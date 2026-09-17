// Shared building blocks used by more than one feature module. Kept deliberately small: the
// Item model, the ItemRepository contract, and AppTopBar (used by nearly every screen). Both
// :app and :itemdetails depend on this module, which is what lets :itemdetails exist without
// depending on :app - the only shared code it needs comes from here instead.
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.tecfort.navigationtest.core"
    compileSdk {
        version = release(37) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        minSdk = 26
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.ui)
    // ItemRepository's methods return Flow<...>.
    implementation(libs.kotlinx.coroutines.core)
}
