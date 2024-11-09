package net.twisterrob.colorfilters.build

import net.twisterrob.colorfilters.build.dsl.android
import net.twisterrob.colorfilters.build.dsl.autoNamespace
import net.twisterrob.colorfilters.build.dsl.libs

plugins {
	id("net.twisterrob.gradle.plugin.quality")
	id("net.twisterrob.colorfilters.build.detekt")
	id("net.twisterrob.colorfilters.build.kotlin")
	id("net.twisterrob.colorfilters.build.android.dex-limit")
	id("net.twisterrob.colorfilters.build.android.androidTest")
	id("net.twisterrob.colorfilters.build.android.unitTest")
}

android {
	namespace = project.autoNamespace
	afterEvaluate {
		check(namespace?.contains("_") == false) {
			"Android namespace must not contain underscores: ${project.path} -> ${namespace}"
		}
	}

	@Suppress("MagicNumber")
	defaultConfig {
		minSdk = libs.versions.android.minSdk.map(String::toInt).get()
		compileSdk = libs.versions.android.compileSdk.map(String::toInt).get()
	}
	lint {
		// Be strict with any lint problems.
		warningsAsErrors = true
		// Lint is run on CI, so no need to run lintVitalRelease on assemble.
		checkReleaseBuilds = false

		// REPORT this is not working in AS
		// workaround? java.nio.file.Files.createSymbolicLink in settings.gradle
		lintConfig = rootProject.file("config/lint/lint.xml")

		val cleanPath = project.path.substring(1).replace(':', '+')
		baseline = rootProject.file("config/lint/baseline/${cleanPath}.xml")
	}
}
