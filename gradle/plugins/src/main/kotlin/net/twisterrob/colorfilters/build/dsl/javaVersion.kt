package net.twisterrob.colorfilters.build.dsl

import org.gradle.accessors.dm.LibrariesForLibs.VersionAccessors
import org.gradle.api.JavaVersion

internal val VersionAccessors.javaVersion: JavaVersion
	get() = JavaVersion.toVersion(this.java.get())
