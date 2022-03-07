plugins {
	id("net.twisterrob.android-library")
	id("build.android-base.conventions")
}

dependencies {
	implementation(project(":feature:base"))

	testImplementation(project(":component:test-base-unit"))

	androidTestImplementation(project(":component:test-base-ui"))
	androidTestImplementation(project("${project.path}:test-fixtures"))
}
