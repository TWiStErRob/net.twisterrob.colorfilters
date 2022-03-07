import com.android.build.api.dsl.ApplicationDefaultConfig
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryDefaultConfig
import com.android.build.api.dsl.LibraryExtension
import com.android.build.gradle.AppExtension
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import io.gitlab.arturbosch.detekt.report.ReportMergeTask
import net.twisterrob.gradle.android.androidComponents

plugins {
	id("kotlin-android")
	id("kotlin-kapt")
	id("net.twisterrob.quality")
	id("io.gitlab.arturbosch.detekt")
}

val javaVersion = JavaVersion.VERSION_1_8

tasks.withType<JavaCompile> {
	sourceCompatibility = javaVersion.toString()
	targetCompatibility = javaVersion.toString()
	options.compilerArgs = options.compilerArgs + "-Xlint:all"
	options.compilerArgs = options.compilerArgs + "-Werror"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
	kotlinOptions {
		jvmTarget = javaVersion.toString()
		allWarningsAsErrors = true
	}
}

@Suppress("UnstableApiUsage")
(project.extensions["android"] as CommonExtension<*, *, *, *>).apply android@{
	@Suppress("MagicNumber")
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

// Central Kotlin configuration.
run {
	val VERSION_KOTLIN: String by project.properties
	dependencies {
		add("implementation", platform("org.jetbrains.kotlin:kotlin-bom:${VERSION_KOTLIN}"))
		add("implementation", "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${VERSION_KOTLIN}")
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

(project.extensions.getByName<DetektExtension>("detekt")).apply {
	ignoreFailures = true
	buildUponDefaultConfig = true
	allRules = true
	config = rootProject.files("config/detekt/detekt.yml")
	baseline = rootProject.file("config/detekt/detekt-baseline-${project.name}.xml")
	basePath = rootProject.projectDir.absolutePath

	parallel = true

	tasks.withType<Detekt>().configureEach {
		// Target version of the generated JVM bytecode. It is used for type resolution.
		jvmTarget = javaVersion.toString()
		reports {
			html.required.set(true) // human
			xml.required.set(true) // checkstyle
			txt.required.set(true) // console
			// https://sarifweb.azurewebsites.net
			sarif.required.set(true) // Github Code Scanning
		}
	}
}

val detektReportMergeSarif = rootProject.tasks.named<ReportMergeTask>("detektReportMergeSarif")
tasks.withType<Detekt> {
	finalizedBy(detektReportMergeSarif)
	detektReportMergeSarif.configure { input.from(this@withType.sarifReportFile) }
}

val detektReportMergeXml = rootProject.tasks.named<ReportMergeTask>("detektReportMergeXml")
tasks.withType<Detekt> {
	finalizedBy(detektReportMergeXml)
	detektReportMergeXml.configure { input.from(this@withType.xmlReportFile) }
}
