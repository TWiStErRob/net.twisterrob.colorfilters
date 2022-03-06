import com.android.build.api.dsl.ApplicationDefaultConfig
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryDefaultConfig
import com.android.build.api.dsl.LibraryExtension
import com.android.build.gradle.AppExtension
import net.twisterrob.gradle.android.androidComponents

plugins {
	id("net.twisterrob.kotlin")
	id("net.twisterrob.quality")
}

tasks.withType<JavaCompile> {
	options.compilerArgs = options.compilerArgs + "-Xlint:all"
	options.compilerArgs = options.compilerArgs + "-Werror"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
	kotlinOptions {
		allWarningsAsErrors = true
	}
}

@Suppress("UnstableApiUsage")
(project.extensions["android"] as CommonExtension<*, *, *, *>).apply android@{
	defaultConfig {
		minSdk = 14
		if (this@android is AppExtension) {
			this@defaultConfig as ApplicationDefaultConfig
			targetSdk = 31
		}
		compileSdk = 31
	}
	defaultConfig {
		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
		baseline = rootProject.file("config/lint/baseline ${cleanPath}.xml")
		// TODEL https://issuetracker.google.com/issues/170658134
		androidComponents.finalizeDsl {
			if (buildFeatures.viewBinding == true || project.path == ":app") {
				disable += "UnusedIds"
			}
		}
	}
	if (this@android is LibraryExtension) {
		// Disable BuildConfig class generation for features and components, we only need it in :app.
		buildFeatures.buildConfig = false
	}
}

// Central Kotlin configuration.
run {
	val VERSION_KOTLIN: String by project.properties
	dependencies {
		implementation(platform("org.jetbrains.kotlin:kotlin-bom:${VERSION_KOTLIN}"))
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
				val min = tasks.named("generateReleaseMinificationRules")
				lintTasks.configureEach { dependsOn(min) }
			}
		}
	}
}
