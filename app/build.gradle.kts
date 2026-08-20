plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "ru.gft.termux2ssh"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "ru.gft.termux2ssh"
        minSdk = 24
        targetSdk = 37
        versionCode = 2
        versionName = "1.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    splits {
        abi {
            isEnable = true
            reset()
            include("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
            isUniversalApk = true
        }
    }
}

dependencies {
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}
androidComponents {
    onVariants { variant ->
        variant.outputs.forEach { output ->
            val abiName = output.filters
                .find { it.filterType  == com.android.build.api.variant.FilterConfiguration.FilterType.ABI }
                .let { it?.identifier ?: "universal"}

            val baseName = "T2SSH"
            val buildType = variant.buildType ?: "release"
            val versionName = android.defaultConfig.versionName ?: "1.1"

            output.outputFileName.set("${baseName}-${buildType}-${abiName}-v${versionName}.apk")
        }
    }
}