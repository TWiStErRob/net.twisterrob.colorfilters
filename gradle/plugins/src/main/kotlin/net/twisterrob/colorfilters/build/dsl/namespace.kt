package net.twisterrob.colorfilters.build.dsl

import org.gradle.api.Project

val Project.autoNamespace: String
	get() {
		val subpackage = project
			.path
			.removePrefix(":feature")
			.removePrefix(":component")
			.replace(":", ".")
		return "net.twisterrob.colorfilters.android${subpackage}"
	}
