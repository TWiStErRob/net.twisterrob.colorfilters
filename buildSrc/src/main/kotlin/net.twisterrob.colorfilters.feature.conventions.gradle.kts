plugins {
	id("net.twisterrob.android-library")
	id("build.android-base.conventions")
}

dependencies {
	implementation(project(":feature:base"))

	androidTestImplementation(project(":test-base"))
	androidTestImplementation(project("${project.path}:test-fixtures"))
}
