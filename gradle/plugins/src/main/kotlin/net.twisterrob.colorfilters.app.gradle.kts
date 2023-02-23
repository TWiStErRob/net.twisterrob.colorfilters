plugins {
	id("net.twisterrob.android-app")
	id("net.twisterrob.colorfilters.build.android-base")
}

dependencies {
	androidTestImplementation(project(":component:test-base-ui"))
}

android {
	defaultConfig {
		targetSdk = 32
	}
	lint {
		checkDependencies = true
	}
}
