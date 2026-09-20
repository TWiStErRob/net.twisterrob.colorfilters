package net.twisterrob.colorfilters.build.dsl

import org.gradle.api.Action
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

/**
 * Configures AGP's built-in Kotlin extension without requiring a generated type-safe accessor.
 */
internal fun Project.kotlin(block: Action<KotlinAndroidProjectExtension>) {
	extensions.configure(KotlinAndroidProjectExtension::class.java, block)
}
