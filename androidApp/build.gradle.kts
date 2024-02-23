plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    kotlin("plugin.serialization") version "1.9.0"
    id("com.google.firebase.crashlytics")
    id("com.google.gms.google-services")
}

android {
    signingConfigs {
        getByName("debug") {
            storeFile = file("/Users/rupestre/Documents/rutappa/rutappa")
            storePassword = "rutappa.2024"
            keyAlias = "rutappa"
            keyPassword = "rutappa.2024"
        }
        create("release") {
            storeFile = file("/Users/rupestre/Documents/rutappa/rutappa")
            storePassword = "rutappa.2024"
            keyAlias = "rutappa"
            keyPassword = "rutappa.2024"
        }
    }
    namespace = "com.rgr.rutappa.android"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.rgr.rutappa.android"
        minSdk = 27
        targetSdk = 34
        versionCode = 20240225
        versionName = "1.0.5"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            //isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
            buildConfigField("String", "clientId", "\"747962270451-pmm32sbgqdfh0gvg8gm7osc6t2f0mr6v.apps.googleusercontent.com\"")
        }
        getByName("debug") {
            isDebuggable = true
            buildConfigField("String", "clientId", "\"747962270451-pmm32sbgqdfh0gvg8gm7osc6t2f0mr6v.apps.googleusercontent.com\"")
        }
    }
    flavorDimensions.add("version")
    productFlavors {
        create("dev") {
            dimension = "version"
            applicationIdSuffix = ".debug"
        }
        create("pro") {
            dimension = "version"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        buildConfig = true
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(projects.shared)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.androidx.activity.compose)
    debugImplementation(libs.compose.ui.tooling)
    //viewModel
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    //koin
    implementation(libs.koin.androidx.compose)
    //navigation
    implementation(libs.androidx.navigation.compose)
    //fonts
    implementation(libs.androidx.ui.text.google.fonts)
    //datastore
    implementation(libs.androidx.data.store.core)
    //coil
    implementation(libs.coil.compose)
    //permissions
    implementation(libs.accompanist.permissions)
    //chrome custom tab
    implementation(libs.androidx.browser)
    //splash
    implementation(libs.androidx.core.splashscreen)
    //serialization
    implementation(libs.kotlinx.serialization.json)
    //firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.1"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-config-ktx")
    implementation("com.google.firebase:firebase-firestore")
    implementation(libs.play.services.auth)
    //kmm result
    implementation("at.asitplus:kmmresult:1.5.4")
}