import net.twisterrob.colorfilters.build.dsl.autoNamespace

plugins {
	id("net.twisterrob.gradle.plugin.android-library")
	id("net.twisterrob.colorfilters.build.android.base")
}

val owningModule = project(project.path.removeSuffix(":test-fixtures"))

dependencies {
	compileOnly(owningModule)
	compileOnly(project(":component:test-base-ui"))

	androidTestImplementation(project(":component:test-base-ui"))
}

android {
	namespace = "${owningModule.autoNamespace}.fixtures"
	@Suppress("UnstableApiUsage")
	buildFeatures.buildConfig = false
}
