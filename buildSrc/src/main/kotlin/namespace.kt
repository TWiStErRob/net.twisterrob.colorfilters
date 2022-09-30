import org.gradle.api.Project

val Project.namespace: String
	get() = "net.twisterrob.colorfilters.android.${project.name}"
