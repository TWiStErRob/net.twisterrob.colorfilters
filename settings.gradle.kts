import net.twisterrob.colorfilters.build.dsl.isCI
import net.twisterrob.gradle.doNotNagAbout
import net.twisterrob.gradle.settings.enableFeaturePreviewQuietly

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
				includeGroup("com.gradle.develocity")
			}
		}
	}
}

plugins {
	id("com.gradle.develocity") version "3.19"
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

develocity {
	buildScan {
		termsOfUseUrl = "https://gradle.com/help/legal-terms-of-use"
		termsOfUseAgree = "yes"
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

// TODEL Gradle 8.8 sync in IDEA 2024.1.4 https://youtrack.jetbrains.com/issue/IDEA-353787.
@Suppress("MaxLineLength", "StringLiteralDuplication")
if ((System.getProperty("idea.version") ?: "") < "2024.2") {
	doNotNagAbout(
		"The CopyProcessingSpec.getFileMode() method has been deprecated. " +
				"This is scheduled to be removed in Gradle 9.0. " +
				"Please use the getFilePermissions() method instead. " +
				"Consult the upgrading guide for further information: " +
				"https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_8.html#unix_file_permissions_deprecated",
	)
	doNotNagAbout(
		"The CopyProcessingSpec.getDirMode() method has been deprecated. " +
				"This is scheduled to be removed in Gradle 9.0. " +
				"Please use the getDirPermissions() method instead. " +
				"Consult the upgrading guide for further information: " +
				"https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_8.html#unix_file_permissions_deprecated",
	)
} else {
	val error: (String) -> Unit = (if (isCI) ::error else logger::warn)
	error("Android Studio version changed, please review hack.")
}
