package net.twisterrob.colorfilters.build.dsl

import org.gradle.api.Project

internal val Project.autoNamespace: String
	get() {
		val subpackage = project
			.path
			// Make sure features and components have unique names.
			.removePrefix(":feature")
			.removePrefix(":component")
			// Make sure the ID is always a valid Java identifier.
			.replace(Regex("[^:a-z]"), "_")
			// Convert Gradle path to Java package.
			.replace(":", ".")
		return "net.twisterrob.colorfilters.android${subpackage}"
	}
