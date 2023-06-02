package net.twisterrob.colorfilters.build

import com.android.build.gradle.AppExtension
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
//<editor-fold desc="Desugaring">
// Desugaring setup so that we can use Java 8/11 features on lower APIs.
// e.g. to support Mockito 5.x on API 23 and lower.
plugins.withId("com.android.base") {
	// Disabled for now, because it's only needed for lower API levels.
	// And Android-JUnit5 has a 26 lower bound.
	@Suppress("ConstantConditionIf")
	if (true) return@withId
	android {
		compileOptions {
			isCoreLibraryDesugaringEnabled = true
		}
		dependencies {
			add("coreLibraryDesugaring", libs.android.desugar)
		}
		(this@android as? AppExtension)?.apply {
			defaultConfig.multiDexEnabled = true
		}
	}
}
//</editor-fold>
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
