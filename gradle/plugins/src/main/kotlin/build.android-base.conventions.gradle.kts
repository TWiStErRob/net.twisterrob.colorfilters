import com.android.build.api.dsl.ApplicationDefaultConfig
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryDefaultConfig
import com.android.build.api.dsl.LibraryExtension
import com.android.build.gradle.AppExtension
import com.android.build.gradle.internal.tasks.DexMergingTask
import net.twisterrob.gradle.android.androidComponents

plugins {
	id("net.twisterrob.quality")
	id("net.twisterrob.colorfilters.build.detekt")
	id("net.twisterrob.colorfilters.build.kotlin")
}

@Suppress("UnstableApiUsage")
(project.extensions["android"] as CommonExtension<*, *, *, *>).apply android@{
	namespace = project.namespace

	@Suppress("MagicNumber")
	defaultConfig {
		minSdk = 14
		if (this@android is AppExtension) {
			this@defaultConfig as ApplicationDefaultConfig
			targetSdk = 32
		}
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

// Prevent the following error by allowing only a few parallel executions of these tasks:
// > com.android.builder.dexing.DexArchiveMergerException: Error while merging dex archives:
// > Caused by: java.lang.OutOfMemoryError: Java heap space
// > Expiring Daemon because JVM heap space is exhausted
@Suppress("MagicNumber")
val instances = (Runtime.getRuntime().maxMemory() / 1e9 - 1).toInt().coerceAtLeast(1)
registerLimitTasksService("dexMergingTaskLimiter", instances)
afterEvaluate { // To get numberOfBuckets populated.
	tasks.withType<DexMergingTask>().configureEach {
		if (numberOfBuckets.get() == 1) { // Implies DexMergingAction.MERGE_ALL|MERGE_EXTERNAL_LIBS.
			@Suppress("UnstableApiUsage")
			usesService(gradle.sharedServices.registrations.getAt("dexMergingTaskLimiter").service)
		}
	}
}
