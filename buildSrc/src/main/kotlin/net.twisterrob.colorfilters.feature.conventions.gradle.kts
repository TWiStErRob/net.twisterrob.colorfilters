plugins {
	id("net.twisterrob.android-library")
	id("net.twisterrob.kotlin")
	id("net.twisterrob.quality")
}

dependencies {
	implementation(project(":feature:base"))

	androidTestImplementation(project(":test-base"))
	androidTestImplementation(project("${project.path}:test-fixtures"))
}
