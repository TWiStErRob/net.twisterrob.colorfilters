import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension

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
		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
	}
	lint {
		warningsAsErrors = true
		checkReleaseBuilds = false
		// REPORT this is not working in AS
		// workaround? java.nio.file.Files.createSymbolicLink in settings.gradle
		lintConfig = rootProject.file("config/lint/lint.xml")
		val cleanPath = project.path.substring(1).replace(':', '+')
		baseline = rootProject.file("config/lint/baseline ${cleanPath}.xml")
	}
	if (this@android is LibraryExtension) {
		defaultConfig {
			// Enable multidex for all libraries.
			// This will transfer to androidTest apps in those libraries, but not the app.
			multiDexEnabled = true
		}
		buildFeatures.buildConfig = false
	}
	val VERSION_KOTLIN: String by project.properties
	configurations.all {
		resolutionStrategy {
			force("org.jetbrains.kotlin:kotlin-stdlib:${VERSION_KOTLIN}")
			force("org.jetbrains.kotlin:kotlin-stdlib-jdk7:${VERSION_KOTLIN}")
			exclude(group = "androidx.legacy")
		}
	}
}

// TODEL https://github.com/TWiStErRob/net.twisterrob.gradle/issues/214
afterEvaluate {
	val lint = project.tasks.withType<com.android.build.gradle.internal.lint.LintModelWriterTask>()
	val ex = tasks.named("extractMinificationRules")
	lint.configureEach { dependsOn(ex) }
	if (project.path == ":app") {
		afterEvaluate { // double-jump is required because this gets applied before the build plugin.
			val min = tasks.named("generateReleaseMinificationRules")
			lint.configureEach { dependsOn(min) }
		}
	}
}
