@file:Suppress("PropertyName")

import java.util.Properties

plugins {
	`kotlin-dsl`
}
repositories {
	google()
	mavenCentral()
}

val props = Properties().apply {
	load(file("../gradle.properties").inputStream())
}

val VERSION_KOTLIN: String by props
val VERSION_AGP: String by props
val VERSION_PLUGIN_QUALITY: String by props
val VERSION_PLUGIN_ANDROID: String by props
val VERSION_MOCKITO: String by props
val VERSION_JUNIT: String by props

dependencies {
	compileOnly(gradleApi())
	implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${VERSION_KOTLIN}")

	configurations.all { resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.SECONDS) /* -SNAPSHOT */ }
	implementation("com.android.tools.build:gradle:${VERSION_AGP}")
	implementation("net.twisterrob.gradle:twister-quality:${VERSION_PLUGIN_QUALITY}")
	implementation("net.twisterrob.gradle:twister-convention-plugins:${VERSION_PLUGIN_ANDROID}")

	testImplementation("junit:junit:${VERSION_JUNIT}")
	testImplementation("org.mockito:mockito-core:${VERSION_MOCKITO}")
	testImplementation(gradleApi())
}
