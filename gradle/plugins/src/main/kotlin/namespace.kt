import org.gradle.api.Project

val Project.namespace: String
	get() {
		val subpackage = project
			.path
			.removePrefix(":feature")
			.removePrefix(":component")
			.replace(":", ".")
		return "net.twisterrob.colorfilters.android${subpackage}"
	}
