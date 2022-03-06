plugins {
	id("net.twisterrob.android-library")
	id("net.twisterrob.kotlin")
	id("net.twisterrob.quality")
}

dependencies {
	compileOnly(project(project.path.removeSuffix(":test-fixtures")))
	compileOnly(project(":test-base"))
}
