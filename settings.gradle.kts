import net.twisterrob.gradle.doNotNagAbout
import net.twisterrob.gradle.settings.enableFeaturePreviewQuietly
import net.twisterrob.colorfilters.build.dsl.isCI

rootProject.name = "ColorFilters"

enableFeaturePreviewQuietly("TYPESAFE_PROJECT_ACCESSORS", "Type-safe project accessors")

include(":app")

include(":feature:about", ":feature:about:test-fixtures")

include(":feature:lighting", ":feature:lighting:test-fixtures")
include(":feature:porterduff", ":feature:porterduff:test-fixtures")
include(":feature:matrix", ":feature:matrix:test-fixtures")
include(":feature:palette", ":feature:palette:test-fixtures")
include(":feature:resfont", ":feature:resfont:test-fixtures")

include(":feature:base")
include(":component:core")
include(":component:test-base-ui")
include(":component:test-base-unit")

include(":feature:image")
include(":feature:keyboard", ":feature:keyboard:contract")

pluginManagement {
	includeBuild("gradle/plugins")

	repositories {
		google {
			content {
				includeGroupByRegex("""^com\.android(\..*)?$""")
				includeGroupByRegex("""^com\.google\..*$""")
				includeGroupByRegex("""^androidx\..*$""")
			}
		}
		mavenCentral()
		gradlePluginPortal {
			content {
				includeGroup("com.gradle")
				includeGroup("com.gradle.enterprise")
			}
		}
	}
}

plugins {
	id("com.gradle.enterprise") version "3.15.1"
	id("net.twisterrob.gradle.plugin.nagging")
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
	repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
	repositories {
		google {
			content {
				includeGroupByRegex("""^com\.android(\..*)?$""")
				includeGroupByRegex("""^com\.google\..*$""")
				includeGroupByRegex("""^androidx\..*$""")
			}
		}
		mavenCentral()
	}
}

gradleEnterprise {
	buildScan {
		termsOfServiceUrl = "https://gradle.com/terms-of-service"
		termsOfServiceAgree = "yes"
		if (isCI) {
			fun setOutput(name: String, value: Any?) {
				// Using `appendText` to make sure out outputs are not cleared.
				// Using `\n` to make sure further outputs are correct.
				// Using `delimiter` to ensure that any special characters (such as newlines) are escaped.
				val delimiter = java.util.UUID.randomUUID().toString()
				providers
					.environmentVariable("GITHUB_OUTPUT")
					.map(::File)
					// TODEL https://github.com/gradle/gradle/issues/25716
					.orNull.let { it ?: error("GITHUB_OUTPUT environment variable is not set.") }
					.appendText("${name}<<${delimiter}\n${value}\n${delimiter}\n")
			}

			buildScanPublished {
				setOutput("build-scan-url", buildScanUri.toASCIIString())
			}
			gradle.addBuildListener(object : BuildAdapter() {
				@Deprecated("Won't work with configuration caching.")
				override fun buildFinished(result: BuildResult) {
					setOutput("result-success", result.failure == null)
					setOutput("result-text", resultText(result))
				}

				private fun resultText(result: BuildResult): String =
					"${result.action} ${resultText(result.failure)}"

				private fun resultText(ex: Throwable?): String =
					when (ex) {
						null ->
							"Successful"
						is org.gradle.internal.exceptions.LocationAwareException ->
							// Shorten the trivial part of the file name,
							// as there's a length limitation in GitHub Actions for message (140 chars).
							// > Build Failed: Build file '.../build.gradle.kts' line: 20
							// > A problem occurred configuring project ':feature:about'.
							"Failed: ${ex.message?.replace(rootDir.absolutePath, "")}"
						else ->
							"Failed with ${ex}"
					}
			})
		}
	}
}

val gradleVersion: String = GradleVersion.current().version

// TODEL Gradle sync in AS EE 2022.1.1 / AS GI 2022.3.1, maybe fixed in AS H.
// https://youtrack.jetbrains.com/issue/IDEA-306975
@Suppress("MaxLineLength")
doNotNagAbout(
	"The AbstractArchiveTask.archivePath property has been deprecated. " +
		"This is scheduled to be removed in Gradle 9.0. " +
		"Please use the archiveFile property instead. " +
		"For more information, please refer to " +
		"https://docs.gradle.org/${gradleVersion}/dsl/org.gradle.api.tasks.bundling.AbstractArchiveTask.html#org.gradle.api.tasks.bundling.AbstractArchiveTask:archivePath" +
		" in the Gradle documentation.",
	"at org.jetbrains.plugins.gradle.tooling.builder.ExternalProjectBuilderImpl\$_getSourceSets_closure"
)

// TODEL Gradle sync in AS EE 2022.1.1 / AS GI 2022.3.1, maybe fixed in AS H.
// https://youtrack.jetbrains.com/issue/IDEA-306975
@Suppress("MaxLineLength")
doNotNagAbout(
	"The AbstractArchiveTask.archivePath property has been deprecated. " +
		"This is scheduled to be removed in Gradle 9.0. " +
		"Please use the archiveFile property instead. " +
		"For more information, please refer to " +
		"https://docs.gradle.org/${gradleVersion}/dsl/org.gradle.api.tasks.bundling.AbstractArchiveTask.html#org.gradle.api.tasks.bundling.AbstractArchiveTask:archivePath" +
		" in the Gradle documentation.",
	"at org.jetbrains.plugins.gradle.tooling.util.SourceSetCachedFinder.createArtifactsMap"
)

// TODEL Gradle 8.2 sync in AS FL 2022.2.1 / AS GI 2022.3.1 / IDEA 2023.1, fixed in 2023.2.
// https://youtrack.jetbrains.com/issue/IDEA-320266
@Suppress("MaxLineLength")
if ((System.getProperty("idea.version") ?: "") < "2023.2") {
	doNotNagAbout(
		"The org.gradle.api.plugins.JavaPluginConvention type has been deprecated. " +
			"This is scheduled to be removed in Gradle 9.0. " +
			"Consult the upgrading guide for further information: " +
			"https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_8.html#java_convention_deprecation",
		"at org.jetbrains.kotlin.idea.gradleTooling.KotlinTasksPropertyUtilsKt.getPureKotlinSourceRoots(KotlinTasksPropertyUtils.kt:59)"
	)
	doNotNagAbout(
		"The Project.getConvention() method has been deprecated. " +
			"This is scheduled to be removed in Gradle 9.0. " +
			"Consult the upgrading guide for further information: " +
			"https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_8.html#deprecated_access_to_conventions",
		"at org.jetbrains.kotlin.idea.gradleTooling.KotlinTasksPropertyUtilsKt.getPureKotlinSourceRoots(KotlinTasksPropertyUtils.kt:59)"
	)
	doNotNagAbout(
		"The org.gradle.api.plugins.Convention type has been deprecated. " +
			"This is scheduled to be removed in Gradle 9.0. " +
			"Consult the upgrading guide for further information: " +
			"https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_8.html#deprecated_access_to_conventions",
		"at org.jetbrains.kotlin.idea.gradleTooling.KotlinTasksPropertyUtilsKt.getPureKotlinSourceRoots(KotlinTasksPropertyUtils.kt:59)"
	)

	doNotNagAbout(
		"The Project.getConvention() method has been deprecated. " +
			"This is scheduled to be removed in Gradle 9.0. " +
			"Consult the upgrading guide for further information: " +
			"https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_8.html#deprecated_access_to_conventions",
		"at org.jetbrains.plugins.gradle.tooling.builder.ProjectExtensionsDataBuilderImpl.buildAll(ProjectExtensionsDataBuilderImpl.groovy:40)"
	)
	doNotNagAbout(
		"The org.gradle.api.plugins.Convention type has been deprecated. " +
			"This is scheduled to be removed in Gradle 9.0. " +
			"Consult the upgrading guide for further information: " +
			"https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_8.html#deprecated_access_to_conventions",
		"at org.jetbrains.plugins.gradle.tooling.builder.ProjectExtensionsDataBuilderImpl.buildAll(ProjectExtensionsDataBuilderImpl.groovy:41)"
	)
	doNotNagAbout(
		"The org.gradle.api.plugins.JavaPluginConvention type has been deprecated. " +
			"This is scheduled to be removed in Gradle 9.0. " +
			"Consult the upgrading guide for further information: " +
			"https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_8.html#java_convention_deprecation",
		// at org.jetbrains.plugins.gradle.tooling.builder.ExternalProjectBuilderImpl.doBuild(ExternalProjectBuilderImpl.groovy:108)
		// at org.jetbrains.plugins.gradle.tooling.builder.ExternalProjectBuilderImpl.doBuild(ExternalProjectBuilderImpl.groovy:117)
		// at org.jetbrains.plugins.gradle.tooling.builder.ExternalProjectBuilderImpl.doBuild(ExternalProjectBuilderImpl.groovy:118)
		"at org.jetbrains.plugins.gradle.tooling.builder.ExternalProjectBuilderImpl.doBuild(ExternalProjectBuilderImpl.groovy:1"
	)
	// No method and line number in stack to match all these:
	//  * JavaPluginUtil.getJavaPluginConvention(JavaPluginUtil.java:13)
	//  * JavaPluginUtil.getSourceSetContainer(JavaPluginUtil.java:18)
	//  * JavaPluginUtil.getSourceSetContainer(JavaPluginUtil.java:19)
	doNotNagAbout(
		"The org.gradle.api.plugins.JavaPluginConvention type has been deprecated. " +
			"This is scheduled to be removed in Gradle 9.0. " +
			"Consult the upgrading guide for further information: " +
			"https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_8.html#java_convention_deprecation",
		"at org.jetbrains.plugins.gradle.tooling.util.JavaPluginUtil."
	)
	doNotNagAbout(
		"The Project.getConvention() method has been deprecated. " +
			"This is scheduled to be removed in Gradle 9.0. " +
			"Consult the upgrading guide for further information: " +
			"https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_8.html#deprecated_access_to_conventions",
		"at org.jetbrains.plugins.gradle.tooling.util.JavaPluginUtil."
	)
	doNotNagAbout(
		"The org.gradle.api.plugins.Convention type has been deprecated. " +
			"This is scheduled to be removed in Gradle 9.0. " +
			"Consult the upgrading guide for further information: " +
			"https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_8.html#deprecated_access_to_conventions",
		"at org.jetbrains.plugins.gradle.tooling.util.JavaPluginUtil."
	)
} else {
	val error: (String) -> Unit = (if (isCI) ::error else logger::warn)
	error("Android Studio version changed, please review hack.")
}

// TODEL Gradle 8.2 vs AGP https://issuetracker.google.com/issues/279306626 fixed in 8.2.0-alpha13.
// Gradle 8.2 M1 added nagging for BuildIdentifier.*, which is not replaced in AGP 8.1.x.
@Suppress("MaxLineLength")
if (com.android.Version.ANDROID_GRADLE_PLUGIN_VERSION < "8.2.0") {
	doNotNagAbout( // :lintReportDebug, :lintAnalyzeDebug
		"The BuildIdentifier.isCurrentBuild() method has been deprecated. " +
			"This is scheduled to be removed in Gradle 9.0. " +
			"Use getBuildPath() to get a unique identifier for the build. " +
			"Consult the upgrading guide for further information: " +
			"https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_8.html#build_identifier_name_and_current_deprecation",
		"at com.android.build.gradle.internal.ide.dependencies.BuildMappingUtils.getBuildId(BuildMapping.kt:40)"
	)
	doNotNagAbout( // Android Studio Giraffe Sync https://issuetracker.google.com/issues/279306626#comment4
		"The BuildIdentifier.getName() method has been deprecated. " +
			"This is scheduled to be removed in Gradle 9.0. " +
			"Use getBuildPath() to get a unique identifier for the build. " +
			"Consult the upgrading guide for further information: " +
			"https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_8.html#build_identifier_name_and_current_deprecation",
		"at com.android.build.gradle.internal.ide.dependencies.BuildMappingUtils.getIdString(BuildMapping.kt:48)"
	)
} else {
	val error: (String) -> Unit = (if (isCI) ::error else logger::warn)
	error("AGP version changed, please review hack.")
}
