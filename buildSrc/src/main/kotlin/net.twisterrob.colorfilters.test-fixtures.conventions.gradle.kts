plugins {
	id("net.twisterrob.android-library")
	id("net.twisterrob.kotlin")
}

dependencies {
	compileOnly(project(project.path.removeSuffix(":test-fixtures")))
	compileOnly(project(":test-base"))
}
