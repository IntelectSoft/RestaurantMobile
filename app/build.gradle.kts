
plugins {
    id("com.android.application")
    id("dagger.hilt.android.plugin")
    id("androidx.navigation.safeargs")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    kotlin("android")
    kotlin("kapt")
}

repositories {
    google()
    mavenCentral()
}

android {

    compileSdk = Config.compileSdk

    defaultConfig {
        applicationId = Config.packageName
        minSdk = Config.minSDK
        targetSdk = Config.targetSDK
        versionCode = Config.versionCode
        versionName = Config.versionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

    }

//    buildTypes {
//        debug {
//            isDebuggable = true
//            buildConfigField("Boolean", "enableCrashlytics", "false")
//            isShrinkResources = false
//            isMinifyEnabled = false
//            //signingConfig signingConfigs.release
//        }
//        release {
//            proguardFiles(
//                getDefaultProguardFile("proguard-android-optimize.txt"),
//                "proguard-rules.pro"
//            )
//            buildConfigField("Boolean", "enableCrashlytics", "false")
//            isShrinkResources = true
//            isMinifyEnabled = true
//            isDebuggable = false
////            signingConfig signingConfigs.release
//        }
//    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.0.5"
    }

    fun getValue(key: String): String {
        return project.properties[key].toString()
    }

    flavorDimensions("appType")
    productFlavors {
        create("dev_app") {
            applicationId = getValue("package_name_dev")
            minSdk = 24
            targetSdk = 33
            versionCode = getValue("app_version_code_dev").toInt()
            versionName = getValue("app_version_name_dev")
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            multiDexEnabled = true
            vectorDrawables {
                useSupportLibrary = true
            }
            resValue("string", "app_name", getValue("app_name_dev"))
            buildConfigField("String", "license_api_url", getValue("license_api_url_dev"))
//            buildConfigField("Boolean", "enableCrashlytics", "false")
        }
        create("live_app") {
            applicationId = getValue("package_name_live")
            minSdk = 24
            targetSdk = 33
            versionCode = getValue("app_version_code_live").toInt()
            versionName = getValue("app_version_name_live")
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            multiDexEnabled = true
            vectorDrawables {
                useSupportLibrary = true
            }
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            resValue("string", "app_name", getValue("app_name_live"))
            buildConfigField("String", "license_api_url", getValue("license_api_url_live"))
//            buildConfigField("Boolean", "enableCrashlytics", "true")
        }

        create("stage_app") {
            applicationId = getValue("package_name_stage")
            minSdk = 24
            targetSdk = 33
            versionCode = getValue("app_version_code_stage").toInt()
            versionName = getValue("app_version_name_stage")
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            multiDexEnabled = true
            vectorDrawables {
                useSupportLibrary = true
            }
            resValue("string", "app_name", getValue("app_name_stage"))
            buildConfigField("String", "license_api_url", getValue("license_api_url_stage"))
//            buildConfigField("Boolean", "enableCrashlytics", "true")
        }
    }

    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    namespace = "md.edi.mobilewaiter"
}

dependencies {
    implementation(Dependencies.Android.coreKtx)
    implementation(Dependencies.Android.appCompat)
    implementation(Dependencies.Android.material)
    implementation(Dependencies.Android.constraint)
    implementation(Dependencies.Android.datastore)
    implementation(Dependencies.Android.activityKtx)
    implementation(Dependencies.Android.fragment)

    implementation(Dependencies.Lifecycle.viewModel)
    implementation(Dependencies.Lifecycle.runtime)

    // Hilt
    implementation(Dependencies.Hilt.android)
    implementation(Dependencies.Hilt.coroutines)

    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.3")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("androidx.databinding:databinding-runtime:7.3.1")

    implementation("com.google.firebase:firebase-messaging-ktx:23.1.1")
    implementation("com.google.firebase:firebase-crashlytics-ktx:18.3.2")


    //ROOM
    implementation("androidx.room:room-runtime:2.4.3")
    implementation ("androidx.room:room-ktx:2.4.3")
    annotationProcessor("androidx.room:room-compiler:2.4.3")
    kapt("androidx.room:room-compiler:2.4.3")
    implementation("androidx.appcompat:appcompat:1.5.1")
    implementation("com.google.android.material:material:1.7.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    //for scanner qr codes
    implementation ("com.journeyapps:zxing-android-embedded:4.3.0")

    kapt(Dependencies.Hilt.compiler)

    testImplementation(Dependencies.Test.jUnit)
    androidTestImplementation(Dependencies.Test.androidJUnit)
    androidTestImplementation(Dependencies.Test.espresso)

    implementation(Dependencies.Retrofit.retrofit)
    implementation(Dependencies.Retrofit.okhttp)
    implementation(Dependencies.Retrofit.httpLogging)
    implementation(Dependencies.Retrofit.gson)

    implementation(Dependencies.SDP_SSP.sdp)
    implementation(Dependencies.SDP_SSP.ssp)

    implementation(Dependencies.Paging.runtime)

}