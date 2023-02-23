package net.twisterrob.colorfilters.build

import net.twisterrob.colorfilters.build.dsl.libs
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	id("kotlin-android")
	id("kotlin-kapt")
}

dependencies {
	add("implementation", (platform(libs.kotlin)))
	add("implementation", (libs.kotlin.stdlib.jdk8))
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
	compilerOptions {
		jvmTarget.set(libs.versions.java.map(JvmTarget::fromTarget))
		allWarningsAsErrors.set(true)
	}
}

tasks.withType<JavaCompile>().configureEach {
	sourceCompatibility = libs.versions.java.get()
	targetCompatibility = libs.versions.java.get()
	options.compilerArgs.add("-Xlint:all")
	options.compilerArgs.add("-Werror")
}
