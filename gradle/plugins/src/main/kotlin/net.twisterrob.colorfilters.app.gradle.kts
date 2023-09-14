plugins {
	id("net.twisterrob.gradle.plugin.android-app")
	id("net.twisterrob.colorfilters.build.android.base")
}

dependencies {
	androidTestImplementation(project(":component:test-base-ui"))
}

android {
	defaultConfig {
		targetSdk = 34
	}
	lint {
		checkDependencies = true
	}
	buildFeatures {
		buildConfig = true
	}
}
