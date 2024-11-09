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
		// Workaround for https://youtrack.jetbrains.com/issue/KT-68400
		// > Task :...:kaptGenerateStubsDebugUnitTestKotlin FAILED
		// > w: Kapt currently doesn't support language version 2.0+. Falling back to 1.9.
		// > e: warnings found and -Werror specified
		freeCompilerArgs.add("-Xsuppress-version-warnings")
	}
}

tasks.withType<JavaCompile>().configureEach {
	options.compilerArgs.add("-Xlint:all")
	// TODEL https://issuetracker.google.com/issues/359561906
	// > > Task :feature:base:compileDebugJavaWithJavac FAILED
	// > /modules/java.base/java/lang/Math.class:
	// > /modules/java.base/java/lang/StrictMath.class:
	// > warning: Cannot find annotation method 'value()' in type 'FlaggedApi':
	// > class file for android.annotation.FlaggedApi not found
	options.compilerArgs.add("-Xlint:-classfile")
	options.compilerArgs.add("-Werror")
}
//</editor-fold>
