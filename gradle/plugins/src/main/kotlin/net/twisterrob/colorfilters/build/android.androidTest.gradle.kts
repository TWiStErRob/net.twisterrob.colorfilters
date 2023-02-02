package net.twisterrob.colorfilters.build

import com.android.build.api.dsl.LibraryExtension
import net.twisterrob.colorfilters.build.dsl.android

android {
	defaultConfig {
		dependencies {
			//add("androidTestUtil", "androidx.test.services:test-services:...")
		}
		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		testInstrumentationRunnerArguments["runnerBuilder"] =
			"de.mannodermaus.junit5.AndroidJUnit5Builder"
		//testInstrumentationRunnerArguments["useTestStorageService"] = "true"
	}
	(this@android as? LibraryExtension)?.apply {
		// Enable multidex for all libraries.
		// This will transfer to androidTest apps in those libraries, but not the app.
		defaultConfig.multiDexEnabled = true
	}
	packaging {
		resources {
			excludes.add("META-INF/LICENSE.md")
			excludes.add("META-INF/LICENSE-notice.md")
		}
	}
}
