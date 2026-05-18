import com.google.firebase.appdistribution.gradle.firebaseAppDistribution
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.appdistribution)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    js {
        browser()
        binaries.executable()
        compilations.getByName("main").defaultSourceSet.dependencies {
            implementation(libs.ktor.client.js)
        }
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
            export(projects.shared)
        }
        iosTarget.compilations.getByName("main").defaultSourceSet.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }

    sourceSets {
        commonMain.dependencies {
            // Compose
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.materialIcons)
            implementation(libs.compose.materialIconsExtended)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)

            // Lifecycle & Navigation
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.androidx.navigation.compose)

            // Utils & DI
            implementation(libs.kotlinx.serialization.json)
            api(libs.kotlin.inject.runtime)
            api(projects.shared)

            // Coil
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)

            // Charts
            implementation(libs.charty)
        }

        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.play.services.auth)
            implementation(libs.ktor.client.okhttp)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.duyts.pokerhost"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.duyts.pokerhost"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()

        // Auto-increment version code via CI property, default to 2 for local builds
        versionCode = project.findProperty("VERSION_CODE")?.toString()?.toInt() ?: 2
        versionName = "1.1"
    }

    signingConfigs {
        create("release") {
            val localProperties = Properties()
            val localPropertiesFile = rootProject.file("local.properties")
            if (localPropertiesFile.exists()) {
                localPropertiesFile.inputStream().use { localProperties.load(it) }
            }

            fun getProp(key: String): String? =
                project.findProperty(key)?.toString()
                    ?: System.getenv(key) 
                    ?: localProperties.getProperty(key)

            val keystorePath = getProp("SIGNING_STORE_FILE") ?: "release.jks"

            storeFile = rootProject.file(keystorePath)
            storePassword = getProp("SIGNING_STORE_PASSWORD")
            keyAlias = getProp("SIGNING_KEY_ALIAS")
            keyPassword = getProp("SIGNING_KEY_PASSWORD")
        }
    }

    buildTypes {
        debug {
            isDebuggable = true
            firebaseAppDistribution {
                artifactType = "APK"
                groups = "internal-testers"
                // serviceCredentialsFile = file("firebase-service-account.json").absolutePath
            }
        }

        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            
            val config = signingConfigs.getByName("release")
            if (config.storeFile == null || !config.storeFile!!.exists()) {
                logger.error("Release build requested but signing store file is missing!")
                throw GradleException("Signing store file is missing. Release builds must be signed.")
            }
            signingConfig = config
            firebaseAppDistribution {
                artifactType = "APK"
                groups = "mobile-developer"
                // serviceCredentialsFile = file("firebase-service-account.json").absolutePath
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    debugImplementation(libs.compose.uiTooling)
}
