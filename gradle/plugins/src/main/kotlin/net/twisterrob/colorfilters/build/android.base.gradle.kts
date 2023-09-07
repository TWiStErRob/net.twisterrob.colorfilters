package net.twisterrob.colorfilters.build

import net.twisterrob.colorfilters.build.dsl.android
import net.twisterrob.colorfilters.build.dsl.autoNamespace
import net.twisterrob.gradle.android.androidComponents

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

	@Suppress("MagicNumber")
	defaultConfig {
		minSdk = 14
		compileSdk = 34
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

		// TODEL https://issuetracker.google.com/issues/170658134
		androidComponents.finalizeDsl {
			@Suppress("UnstableApiUsage")
			if (buildFeatures.viewBinding == true || project.path == ":app") {
				disable += "UnusedIds"
			}
		}
	}
}
