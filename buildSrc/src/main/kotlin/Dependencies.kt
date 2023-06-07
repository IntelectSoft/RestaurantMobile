object Dependencies {

    object Hilt {
        const val version = "2.44"
        const val coroutinesAndroid = "1.3.9"
        const val android = "com.google.dagger:hilt-android:$version"
        const val compiler = "com.google.dagger:hilt-compiler:$version"
        const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesAndroid"

    }

    object Android {
        const val coreKtx = "androidx.core:core-ktx:1.8.0"
        const val appCompat = "androidx.appcompat:appcompat:1.7.0-alpha02"
        const val material = "com.google.android.material:material:1.7.0"
        const val constraint = "androidx.constraintlayout:constraintlayout:2.1.4"
        const val datastore = "androidx.datastore:datastore-preferences:1.0.0"
        const val fragment = "androidx.fragment:fragment-ktx:1.5.3"
        const val activityKtx = "androidx.activity:activity-ktx:1.5.1"
    }

    object Lifecycle {
        const val lifecycle = "2.5.1"
        const val viewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle"
        const val runtime = "androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle"
    }

    object Test {
        const val jUnit = "junit:junit:4.13.2"
        const val androidJUnit = "androidx.test.ext:junit:1.1.3"
        const val espresso = "androidx.test.espresso:espresso-core:3.4.0"
    }

    object Retrofit {
        const val retrofit = "com.squareup.retrofit2:retrofit:2.9.0"
        const val okhttp = "com.squareup.okhttp3:okhttp:5.0.0-alpha.2"
        const val gson = "com.squareup.retrofit2:converter-gson:2.9.0"
        const val httpLogging = "com.squareup.okhttp3:logging-interceptor:4.9.1"
    }

    object SDP_SSP {
        const val ssp = "com.intuit.ssp:ssp-android:1.1.0"
        const val sdp = "com.intuit.sdp:sdp-android:1.1.0"
    }

    object Paging {
        const val runtime = "androidx.paging:paging-runtime-ktx:3.0.1"
        const val rxJava = "androidx.paging:paging-rxjava2-ktx:3.0.1"
    }

    object EleNumberPicker{
        const val buttonPicker = "com.cepheuen.elegant-number-button:lib:1.0.2"
    }
}