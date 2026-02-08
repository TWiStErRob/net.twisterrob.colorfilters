package net.twisterrob.colorfilters.build

import net.twisterrob.colorfilters.build.dsl.android
import net.twisterrob.colorfilters.build.dsl.libs
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	id("kotlin-android")
}

dependencies {
	add("implementation", platform(libs.kotlin))
	add("implementation", libs.kotlin.stdlib.jdk8)
}

//<editor-fold desc="Java JVM Source/Target/Release/Toolchain Setup" defaultstate="collapsed">
kotlin {
	compilerOptions {
		jvmTarget = libs.versions.java.map(JvmTarget::fromTarget)
	}
}

tasks.withType<JavaCompile>().configureEach {
	sourceCompatibility = libs.versions.java.get()
	targetCompatibility = libs.versions.java.get()
}

plugins.withId("com.android.base") {
	android {
		compileOptions.apply {
			sourceCompatibility = libs.versions.java.map(JavaVersion::toVersion).get()
			targetCompatibility = libs.versions.java.map(JavaVersion::toVersion).get()
		}
	}
}
//</editor-fold>

//<editor-fold desc="Strict Compilation" defaultstate="collapsed">
kotlin {
	compilerOptions {
		allWarningsAsErrors = true

		// Workaround for https://youtrack.jetbrains.com/issue/KT-68400
		// > Task :...:kaptGenerateStubsDebugUnitTestKotlin FAILED
		// > w: Kapt currently doesn't support language version 2.0+. Falling back to 1.9.
		// > e: warnings found and -Werror specified
		freeCompilerArgs.add("-Xsuppress-version-warnings")

		// Kotlin 2.2 started warning about this:
		// > w: ... This annotation is currently applied to the value parameter only,
		// > but in the future it will also be applied to field.
		// > - To opt in to applying to both value parameter and field,
		// >   add '-Xannotation-default-target=param-property' to your compiler arguments.
		// > - To keep applying to the value parameter only, use the '@param:' annotation target.
		// > See https://youtrack.jetbrains.com/issue/KT-73255 for more details.
		freeCompilerArgs.add("-Xannotation-default-target=param-property")
	}
}

tasks.withType<JavaCompile>().configureEach {
	options.compilerArgs.add("-Xlint:all")
	options.compilerArgs.add("-Werror")
}
//</editor-fold>
