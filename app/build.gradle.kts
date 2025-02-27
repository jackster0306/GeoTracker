plugins {
    id("com.android.application")
    id("com.google.secrets_gradle_plugin") version "0.6"
}

android {
    namespace = "com.psyjg14.coursework2"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.psyjg14.coursework2"
        minSdk = 32
        //noinspection ExpiredTargetSdkVersion
        targetSdk = 32
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

    buildFeatures {
        dataBinding = true
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    //GSON for type conversion
    implementation("com.google.code.gson:gson:2.8.8")

    //Google Maps
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")

    //Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.navigation:navigation-fragment:2.6.0")
    implementation("androidx.navigation:navigation-ui:2.6.0")
    annotationProcessor("androidx.room:room-compiler:2.6.1")


    implementation("androidx.appcompat:appcompat:1.4.0")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    testImplementation("junit:junit:4.12.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")

    implementation("androidx.preference:preference:1.1.1")

    constraints {
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.8.0"){
            because("kotlin-stdlib7-jdk7 is now a part of kotlin-stdlib")
        }
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.0"){
            because("kotlin-stdlib7-jdk8 is now a part of kotlin-stdlib")
        }
    }
}