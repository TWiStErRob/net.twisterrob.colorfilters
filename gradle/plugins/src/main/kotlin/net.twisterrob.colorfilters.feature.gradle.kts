plugins {
	id("net.twisterrob.android-library")
	id("net.twisterrob.colorfilters.build.android.base")
}

dependencies {
	implementation(project(":feature:base"))

	testImplementation(project(":component:test-base-unit"))

	androidTestImplementation(project(":component:test-base-ui"))
	androidTestImplementation(project("test-fixtures"))
}
