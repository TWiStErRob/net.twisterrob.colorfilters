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
	id("com.gradle.develocity") version "4.0.2"
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

// TODEL Gradle 8.13 vs AGP 8.0-8.9 https://issuetracker.google.com/issues/370546370
@Suppress("detekt.MaxLineLength")
doNotNagAbout(
	"Declaring 'crunchPngs' as a property using an 'is-' method with a Boolean type on " +
			"com.android.build.gradle.internal.dsl.BuildType\$AgpDecorated has been deprecated. " +
			"Starting with Gradle 10, this property will no longer be treated like a property. " +
			"The combination of method name and return type is not consistent with Java Bean property rules. " +
			"Add a method named 'getCrunchPngs' with the same behavior and mark the old one with @Deprecated, " +
			"or change the type of 'com.android.build.gradle.internal.dsl.BuildType\$AgpDecorated.isCrunchPngs' (and the setter) to 'boolean'. " +
			"Consult the upgrading guide for further information: " +
			"https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_8.html#groovy_boolean_properties"
)
@Suppress("detekt.MaxLineLength")
doNotNagAbout(
	"Declaring 'useProguard' as a property using an 'is-' method with a Boolean type on " +
			"com.android.build.gradle.internal.dsl.BuildType has been deprecated. " +
			"Starting with Gradle 10, this property will no longer be treated like a property. " +
			"The combination of method name and return type is not consistent with Java Bean property rules. " +
			"Add a method named 'getUseProguard' with the same behavior and mark the old one with @Deprecated, " +
			"or change the type of 'com.android.build.gradle.internal.dsl.BuildType.isUseProguard' (and the setter) to 'boolean'. " +
			"Consult the upgrading guide for further information: " +
			"https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_8.html#groovy_boolean_properties"
)
@Suppress("detekt.MaxLineLength")
doNotNagAbout(
	"Declaring 'wearAppUnbundled' as a property using an 'is-' method with a Boolean type on " +
			"com.android.build.api.variant.impl.ApplicationVariantImpl has been deprecated. " +
			"Starting with Gradle 10, this property will no longer be treated like a property. " +
			"The combination of method name and return type is not consistent with Java Bean property rules. " +
			"Add a method named 'getWearAppUnbundled' with the same behavior and mark the old one with @Deprecated, " +
			"or change the type of 'com.android.build.api.variant.impl.ApplicationVariantImpl.isWearAppUnbundled' (and the setter) to 'boolean'. " +
			"Consult the upgrading guide for further information: " +
			"https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_8.html#groovy_boolean_properties"
)

// TODEL Gradle 8.14 vs AGP 8.9 https://issuetracker.google.com/issues/408334529
@Suppress("detekt.MaxLineLength")
doNotNagAbout(
	"Retrieving attribute with a null key. " +
			"This behavior has been deprecated. " +
			"This will fail with an error in Gradle 10. " +
			"Don't request attributes from attribute containers using null keys. " +
			"Consult the upgrading guide for further information: " +
			"https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_8.html#null-attribute-lookup",
	"at com.android.build.gradle.internal.ide.dependencies.ArtifactUtils.isAndroidProjectDependency(ArtifactUtils.kt:539)",
)
