package net.twisterrob.colorfilters.build.dsl

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.kotlin.dsl.get
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

internal val Project.kotlin: KotlinAndroidProjectExtension
	get() = this.extensions["kotlin"] as KotlinAndroidProjectExtension

internal fun Project.kotlin(block: Action<KotlinAndroidProjectExtension>) {
	block.execute(kotlin)
}
