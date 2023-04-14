plugins {
	id("net.twisterrob.gradle.plugin.android-library")
	id("net.twisterrob.colorfilters.build.android.base")
}

dependencies {
	implementation(project(":feature:base"))

	testImplementation(project(":component:test-base-unit"))

	androidTestImplementation(project(":component:test-base-ui"))
	androidTestImplementation(project("test-fixtures"))
}

android {
	@Suppress("UnstableApiUsage")
	buildFeatures.buildConfig = false
}
