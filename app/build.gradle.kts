plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.firstaidguide"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.firstaidguide"
        minSdk = 21
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.compose.material.icons.extended)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

tasks.register("fixRes") {
    doLast {
        val drawableDir = file("src/main/res/drawable")
        val files = drawableDir.listFiles() ?: return@doLast
        val seen = mutableSetOf<String>()
        files.forEach { file ->
            val name = file.name
            if (name.count { it == '.' } > 1) {
                val lastDotIndex = name.lastIndexOf('.')
                val extension = name.substring(lastDotIndex)
                var baseName = name.substring(0, lastDotIndex)
                while (baseName.contains('.')) {
                    baseName = baseName.substring(0, baseName.lastIndexOf('.'))
                }
                val newName = "$baseName$extension"
                val target = file("src/main/res/drawable/$newName")
                
                if (seen.contains(baseName) || target.exists()) {
                    println("Deleting redundant/duplicate: $name")
                    file.delete()
                } else {
                    println("Renaming $name to $newName")
                    if (file.renameTo(target)) {
                        seen.add(baseName)
                    }
                }
            } else if (file.isFile) {
                val baseName = name.substring(0, name.lastIndexOf('.'))
                seen.add(baseName)
            }
        }
    }
}
