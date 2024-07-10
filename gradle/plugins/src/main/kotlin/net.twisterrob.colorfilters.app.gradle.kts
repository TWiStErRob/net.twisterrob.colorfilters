import net.twisterrob.colorfilters.build.dsl.libs

plugins {
	id("net.twisterrob.gradle.plugin.android-app")
	id("net.twisterrob.colorfilters.build.android.base")
}

dependencies {
	androidTestImplementation(project(":component:test-base-ui"))
}

android {
	defaultConfig {
		targetSdk = libs.versions.android.targetSdk.map(String::toInt).get()
	}
	lint {
		checkDependencies = true
	}
	buildFeatures {
		buildConfig = true
	}
}
