// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    val kotlinVersion = "1.9.21"
    val composeVersion = "1.5.8"
    val agpVersion = "8.2.2"
    
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:$agpVersion")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.48.1")
    }
}

plugins {
    id("io.gitlab.arturbosch.detekt") version "1.23.4" apply false
    id("org.jlleitschuh.gradle.ktlint") version "12.1.0" apply false
    id("com.diffplug.spotless") version "6.25.0" apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

// Apply detekt to root project only
apply(plugin = "io.gitlab.arturbosch.detekt")

// Configure detekt for the root project
detekt {
    config.setFrom("config/detekt/detekt.yml")
    baseline = file("config/detekt/baseline.xml")
    buildUponDefaultConfig = true
    autoCorrect = false
    
    reports {
        html.required.set(true)
        xml.required.set(true)
        txt.required.set(true)
        sarif.required.set(true)
    }
}

// Quality gate task that runs all available static analysis
tasks.register("qualityGate") {
    description = "Runs all quality checks (detekt, ktlint, spotless)"
    group = "verification"
    
    dependsOn(":detekt")
    
    // Add app module quality checks if they exist
    dependsOn(":app:detekt", ":app:ktlintCheck", ":app:spotlessCheck")
}
