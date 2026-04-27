import java.util.Properties

buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.4.0")
    }
    repositories {
        mavenCentral()
        maven {
            url = uri("https://maven.google.com/")
        }
    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.10.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.24" apply false
    id("com.google.dagger.hilt.android") version "2.51.1" apply false
}

fun loadLocalProperty(key: String): String {
    val properties = Properties()
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        file.inputStream().use { properties.load(it) }
    }
    return properties.getProperty(key, "")
}

extra["mapkitApiKey"] = loadLocalProperty("MAPKIT_API_KEY")
extra["geocoderApiKey"] = loadLocalProperty("GEOCODER_API_KEY")
extra["openWeatherApiKey"] = loadLocalProperty("OPEN_WEATHER_API_KEY")