import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.services)
}

val secretos = Properties().apply {
    val archivo = rootProject.file("secrets.properties")
    if (archivo.exists()) archivo.inputStream().use { load(it) }
}
fun secreto(nombre: String): String = secretos.getProperty(nombre)
    ?: throw GradleException(
        "Falta $nombre en secrets.properties (copia secrets.properties.example y llénalo)")

android {
    namespace = "com.example.fakewhatsapp"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.fakewhatsapp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "SUPABASE_URL", "\"${secreto("SUPABASE_URL")}\"")
        buildConfigField("String", "SUPABASE_KEY", "\"${secreto("SUPABASE_KEY")}\"")
    }

    buildFeatures {
        buildConfig = true
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
}

dependencies {
    implementation(libs.activity.ktx)

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.google.code.gson:gson:2.11.0")

    implementation("com.github.bumptech.glide:glide:4.16.0")

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)
}