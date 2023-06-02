package net.twisterrob.colorfilters.build

import net.twisterrob.colorfilters.build.dsl.android
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

//<editor-fold desc="Java JVM Source/Target/Release/Toolchain Setup" defaultstate="collapsed">
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
	compilerOptions {
		jvmTarget.set(libs.versions.java.map(JvmTarget::fromTarget))
	}
}

tasks.withType<JavaCompile>().configureEach {
	sourceCompatibility = libs.versions.java.get()
	targetCompatibility = libs.versions.java.get()
}

plugins.withId("com.android.base") {
	android {
		compileOptions {
			sourceCompatibility = libs.versions.java.map(JavaVersion::toVersion).get()
			targetCompatibility = libs.versions.java.map(JavaVersion::toVersion).get()
		}
	}
}
//</editor-fold>

//<editor-fold desc="Strict Compilation" defaultstate="collapsed">
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
	compilerOptions {
		allWarningsAsErrors.set(true)
	}
}

tasks.withType<JavaCompile>().configureEach {
	options.compilerArgs.add("-Xlint:all")
	options.compilerArgs.add("-Werror")
}
//</editor-fold>
