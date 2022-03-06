plugins {
	id("net.twisterrob.android-library")
	id("build.android-base.conventions")
}

dependencies {
	compileOnly(project(project.path.removeSuffix(":test-fixtures")))
	compileOnly(project(":component:test-base-ui"))
}
