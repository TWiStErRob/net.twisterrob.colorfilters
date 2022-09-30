plugins {
	id("net.twisterrob.android-library")
	id("build.android-base.conventions")
}

val owningModule = project(project.path.removeSuffix(":test-fixtures"))

dependencies {
	compileOnly(owningModule)
	compileOnly(project(":component:test-base-ui"))

	androidTestImplementation(project(":component:test-base-ui"))
}

android {
	namespace = "${owningModule.namespace}.fixtures"
}
