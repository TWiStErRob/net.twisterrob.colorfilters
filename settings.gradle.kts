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

/**
 * @see <a href="https://github.com/gradle/gradle/issues/19069">Feature request</a>
 */
fun Settings.enableFeaturePreviewQuietly(name: String, summary: String) {
	enableFeaturePreview(name)
	val logger: Any = org.gradle.util.internal.IncubationLogger::class.java
		.getDeclaredField("INCUBATING_FEATURE_HANDLER")
		.apply { isAccessible = true }
		.get(null)

	@Suppress("UNCHECKED_CAST")
	val features: MutableSet<String> =
		org.gradle.internal.featurelifecycle.LoggingIncubatingFeatureHandler::class.java
			.getDeclaredField("features")
			.apply { isAccessible = true }
			.get(logger) as MutableSet<String>

	features.add(summary)
}
