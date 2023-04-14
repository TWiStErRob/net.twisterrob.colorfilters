plugins {
	id("net.twisterrob.gradle.plugin.android-library")
	id("net.twisterrob.colorfilters.build.android.base")
}

dependencies {
	if (project.findProject("contract") != null) {
		api(project("contract"))
	}

	testImplementation(project(":component:test-base-unit"))

	androidTestImplementation(project(":component:test-base-ui"))
}

android {
	@Suppress("UnstableApiUsage")
	buildFeatures.buildConfig = false
}
