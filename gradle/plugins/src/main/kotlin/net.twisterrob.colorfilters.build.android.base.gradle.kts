import net.twisterrob.colorfilters.build.dsl.android
import net.twisterrob.colorfilters.build.dsl.autoNamespace
import net.twisterrob.gradle.android.androidComponents

plugins {
	id("net.twisterrob.quality")
	id("net.twisterrob.colorfilters.build.detekt")
	id("net.twisterrob.colorfilters.build.kotlin")
	id("net.twisterrob.colorfilters.build.android.dex-limit")
	id("net.twisterrob.colorfilters.build.android.androidTest")
	id("net.twisterrob.colorfilters.build.android.unitTest")
}

@Suppress("UnstableApiUsage")
android {
	namespace = project.autoNamespace

	@Suppress("MagicNumber")
	defaultConfig {
		minSdk = 14
		compileSdk = 33
	}
	lint {
		warningsAsErrors = true
		checkReleaseBuilds = false
		// REPORT this is not working in AS
		// workaround? java.nio.file.Files.createSymbolicLink in settings.gradle
		lintConfig = rootProject.file("config/lint/lint.xml")
		val cleanPath = project.path.substring(1).replace(':', '+')
		baseline = rootProject.file("config/lint/baseline/${cleanPath}.xml")
		// TODEL https://issuetracker.google.com/issues/170658134
		androidComponents.finalizeDsl {
			if (buildFeatures.viewBinding == true || project.path == ":app") {
				disable += "UnusedIds"
			}
		}
	}
}

// TODEL https://github.com/TWiStErRob/net.twisterrob.gradle/issues/214
run {
	afterEvaluate {
		val lintTasks = project.tasks
			.withType<com.android.build.gradle.internal.lint.LintModelWriterTask>()
		val ex = tasks.named("extractMinificationRules")
		lintTasks.configureEach { dependsOn(ex) }
		if (project.path == ":app") {
			afterEvaluate { // double-jump is required because this gets applied before the build plugin.
				val min = tasks.named("generateReleaseR8MinificationRules")
				lintTasks.configureEach { dependsOn(min) }
			}
		}
	}
}
