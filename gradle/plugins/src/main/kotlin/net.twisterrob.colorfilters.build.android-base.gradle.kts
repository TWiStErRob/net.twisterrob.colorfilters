import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryDefaultConfig
import com.android.build.api.dsl.LibraryExtension
import net.twisterrob.gradle.android.androidComponents

plugins {
	id("net.twisterrob.quality")
	id("net.twisterrob.colorfilters.build.detekt")
	id("net.twisterrob.colorfilters.build.kotlin")
	id("net.twisterrob.colorfilters.build.android-dex-limit")
}

@Suppress("UnstableApiUsage")
(project.extensions["android"] as CommonExtension<*, *, *, *>).apply android@{
	namespace = project.namespace

	@Suppress("MagicNumber")
	defaultConfig {
		minSdk = 14
		compileSdk = 33
	}
	defaultConfig {
		dependencies {
			add("androidTestRuntimeOnly", libs.junit5.android.runner)
			//add("androidTestUtil", "androidx.test.services:test-services:...")
		}
		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		testInstrumentationRunnerArguments["runnerBuilder"] = "de.mannodermaus.junit5.AndroidJUnit5Builder"
		//testInstrumentationRunnerArguments["useTestStorageService"] = "true"

		if (this@android is LibraryExtension) {
			this@defaultConfig as LibraryDefaultConfig
			// Enable multidex for all libraries.
			// This will transfer to androidTest apps in those libraries, but not the app.
			multiDexEnabled = true
		}
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
	packagingOptions {
		resources {
			excludes.add("META-INF/LICENSE.md")
			excludes.add("META-INF/LICENSE-notice.md")
		}
	}
	testOptions {
		unitTests.all {
			it.useJUnitPlatform {
			}
			it.testLogging {
				events("passed", "skipped", "failed")
			}
		}
	}
	if (this@android is LibraryExtension) {
		// Disable BuildConfig class generation for features and components, we only need it in :app.
		buildFeatures.buildConfig = false
	}
}

configurations.all {
	resolutionStrategy.eachDependency {
		if (requested.group == "org.hamcrest" && requested.name == "hamcrest-library") {
			useTarget("${target.group}:hamcrest:${target.version}")
			because("Since 2.2 hamcrest-core and hamcrest-library are deprecated.")
		}
		if (requested.group == "org.hamcrest" && requested.name == "hamcrest-core") {
			useTarget("${target.group}:hamcrest:${target.version}")
			because("Since 2.2 hamcrest-core and hamcrest-library are deprecated.")
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
