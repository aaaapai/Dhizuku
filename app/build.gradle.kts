import org.jetbrains.kotlin.konan.properties.loadProperties
import java.util.Properties

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.agp.app)
    alias(libs.plugins.kotlin)
    id("kotlin-kapt")
    alias(libs.plugins.ksp)
}

val keystoreDir = "$rootDir/keystore"

val keystoreProps = Properties()
for (name in arrayOf("r0s.properties", "debug.properties")) {
    val f = file("$keystoreDir/$name")
    if (!f.exists()) continue
    keystoreProps.load(f.inputStream())
    break
}

android {
    namespace = "com.rosan.dhizuku"

    compileSdk = 34

    defaultConfig {
        minSdk = 24
        targetSdk = 34
        val versionProps = loadProperties("$rootDir/version.properties")
        versionCode = versionProps.getProperty("versionCode").toInt()
        versionName = versionProps.getProperty("versionName")

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        val keyAlias = keystoreProps.getProperty("keyAlias")
        val keyPassword = keystoreProps.getProperty("keyPassword")
        val storeFile = file("$keystoreDir/${keystoreProps.getProperty("storeFile")}")
        val storePassword = keystoreProps.getProperty("storePassword")
        getByName("debug") {
            this.keyAlias = keyAlias
            this.keyPassword = keyPassword
            this.storeFile = storeFile
            this.storePassword = storePassword
            enableV1Signing = false
            enableV2Signing = true
            enableV3Signing = true
        }

        create("release") {
            this.keyAlias = keyAlias
            this.keyPassword = keyPassword
            this.storeFile = storeFile
            this.storePassword = storePassword
            enableV1Signing = false
            enableV2Signing = true
            enableV3Signing = true
        }
    }

    buildTypes {
        getByName("debug") {
            signingConfig = signingConfigs.getByName("debug")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        targetCompatibility = JavaVersion.VERSION_21
        sourceCompatibility = JavaVersion.VERSION_21
    }

    kotlin {
        jvmToolchain(22)
    }

    buildFeatures {
        buildConfig = true
        compose = true
        aidl = true
    }

    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }

    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
    }
}

dependencies {
    compileOnly(project(":hidden-api"))
    implementation(project(":dhizuku-aidl"))
    implementation(project(":dhizuku-shared"))

    implementation(libs.androidx.core)
    implementation(libs.androidx.lifecycle)
    implementation(libs.androidx.activity.compose)
    implementation(libs.compose.ui)
    implementation(libs.compose.material)
    implementation(libs.compose.material3)
    implementation(libs.compose.uiToolingPreview)

    implementation(libs.compose.navigation)
    implementation(libs.compose.materialIcons)

    implementation(libs.room.runtime)
    ksp(libs.room.compiler)
    implementation(libs.room.ktx)

    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.compose)

    implementation(libs.lottie.compose)

    implementation(libs.accompanist)
    implementation(libs.accompanist.navigationAnimation)
    implementation(libs.accompanist.flowlayout)
    implementation(libs.accompanist.drawablepainter)
    implementation(libs.accompanist.systemuicontroller)

    implementation(libs.lsposed.hiddenapibypass)

    implementation(libs.xxpermissions)

    implementation(libs.rikka.shizuku.api)
    implementation(libs.rikka.shizuku.provider)

    implementation(libs.iamr0s.androidAppProcess)
}
