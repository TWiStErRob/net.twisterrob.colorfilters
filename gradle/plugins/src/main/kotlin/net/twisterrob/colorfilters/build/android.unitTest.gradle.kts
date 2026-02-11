package net.twisterrob.colorfilters.build

import net.twisterrob.colorfilters.build.dsl.android

android {
	testOptions.apply {
		unitTests.all {
			it.useJUnitPlatform {
			}
			it.testLogging {
				events("passed", "skipped", "failed")
			}
		}
	}
}

configurations.all {
	resolutionStrategy.eachDependency {
		if (requested.group == "org.hamcrest" && requested.name == "hamcrest-library") {
			useTarget("${target.group}:hamcrest:${target.version}")
			because("Since 2.2 hamcrest-core and hamcrest-library are deprecated.")
		}
		if (requested.group == "org.hamcrest" && requested.name == "hamcrest-core") {
			useTarget("${target.group}:hamcrest:${target.version}")
			because("Since 2.2 hamcrest-core and hamcrest-library are deprecated.")
		}
	}
}
