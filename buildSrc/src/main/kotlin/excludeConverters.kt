import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.ModuleIdentifier
import org.gradle.api.provider.Provider

fun Provider<MinimalExternalModuleDependency>.asGroup(): Map<String, String> =
	this.get().module.asGroup()

fun ModuleIdentifier.asGroup(): Map<String, String> =
	mapOf(
		"group" to this.group
	)

fun Provider<MinimalExternalModuleDependency>.asModule(): Map<String, String> =
	this.get().module.asModule()

fun ModuleIdentifier.asModule(): Map<String, String> =
	mapOf(
		"module" to this.name,
	)

fun Provider<MinimalExternalModuleDependency>.asCoordinate(): Map<String, String> =
	this.get().module.asCoordinate()

fun ModuleIdentifier.asCoordinate(): Map<String, String> =
	mapOf(
		"group" to this.group,
		"module" to this.name,
	)
