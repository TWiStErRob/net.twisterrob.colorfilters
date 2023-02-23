@file:JvmName("VersionCatalogExt")

package net.twisterrob.colorfilters.build.dsl

import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.artifacts.ModuleIdentifier
import org.gradle.api.provider.Provider

fun ModuleDependency.exclude(dep: Provider<MinimalExternalModuleDependency>) {
	this.exclude(dep.asCoordinate())
}

private fun Provider<MinimalExternalModuleDependency>.asCoordinate(): Map<String, String> =
	this.get().module.asCoordinate()

private fun ModuleIdentifier.asCoordinate(): Map<String, String> =
	mapOf(
		"group" to this.group,
		"module" to this.name,
	)

fun ModuleDependency.exclude(
	group: Provider<MinimalExternalModuleDependency>? = null,
	module: Provider<MinimalExternalModuleDependency>? = null
) {
	check((group != null) xor (module != null)) {
		"Usage: exclude(libs.foo), exclude(group = libs.foo) or exclude(module = libs.foo)."
	}
	group?.let { this.exclude(it.asGroup()) }
	module?.let { this.exclude(it.asModule()) }
}

private fun Provider<MinimalExternalModuleDependency>.asGroup(): Map<String, String> =
	this.get().module.asGroup()

private fun ModuleIdentifier.asGroup(): Map<String, String> =
	mapOf(
		"group" to this.group
	)

private fun Provider<MinimalExternalModuleDependency>.asModule(): Map<String, String> =
	this.get().module.asModule()

private fun ModuleIdentifier.asModule(): Map<String, String> =
	mapOf(
		"module" to this.name,
	)
