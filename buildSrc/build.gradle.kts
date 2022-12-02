import org.gradle.kotlin.dsl.`kotlin-dsl`

plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    google()
    maven {
        url = uri("https://plugins.gradle.org/m2/")
    }
}

dependencies {
    implementation("com.android.tools.build:gradle:7.2.1")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.10")
    implementation("com.squareup:javapoet:1.13.0")
    implementation("androidx.navigation:navigation-safe-args-gradle-plugin:2.5.3")
    implementation("com.google.gms:google-services:4.3.14")
    implementation("com.google.firebase:firebase-crashlytics-gradle:2.9.2")

}