import groovy.json.JsonOutput.toJson
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
		maven("https://s01.oss.sonatype.org/content/repositories/snapshots/") {
			name = "Sonatype 01: SNAPSHOTs"
			content {
				includeVersionByRegex("""^net\.twisterrob\.gradle$""", ".*", """.*-SNAPSHOT$""")
				includeVersionByRegex("""^net\.twisterrob\.gradle$""", ".*", """.*-\d{8}\.\d{6}-\d+$""")
			}
			mavenContent {
				// This doesn't allow using specific snapshot, so using versionRegex above.
				//snapshotsOnly()
			}
		}
	}
}

plugins {
	id("com.gradle.enterprise") version "3.11.4"
	id("net.twisterrob.settings")
}

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
		// TODO how to use net.twisterrob.sun.plugins.isCI? 
		if (System.getenv("GITHUB_ACTIONS") == "true") {
			fun setOutput(name: String, value: Any?) {
				// Using `appendText` to make sure out outputs are not cleared.
				// Using `\n` to make sure further outputs are correct.
				// Using `toJson()` to ensure that any special characters (such as newlines) are escaped.
				File(System.getenv("GITHUB_OUTPUT") ?: error("Missing env: GITHUB_OUTPUT"))
					.appendText("${name}=${toJson(value)}\n")
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
							"Failed: ${ex.message}"
						else ->
							"Failed with ${ex}"
					}
			})
		}
	}
}
