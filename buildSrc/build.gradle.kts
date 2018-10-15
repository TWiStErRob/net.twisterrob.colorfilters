@file:Suppress("PropertyName")

import java.util.Properties

plugins {
	kotlin("jvm") version "1.2.71"
}
repositories {
	jcenter()
	google()
	maven { setUrl("http://localhost/maven") }
	maven { name = "Gradle libs (for Kotlin-DSL)"; setUrl("https://repo.gradle.org/gradle/libs-releases-local/") }
	maven { name = "TWiStErRob"; setUrl("https://dl.bintray.com/twisterrob/maven") }
}

configurations.all {
	resolutionStrategy.eachDependency {
		if (requested.group == "org.jetbrains.kotlin" && requested.name == "kotlin-stdlib-jre7") {
			useTarget("${target.group}:kotlin-stdlib-jdk7:${target.version}")
			because("https://issuetracker.google.com/issues/112761162")
		}
		if (requested.group == "org.jetbrains.kotlin" && requested.name == "kotlin-stdlib-jre8") {
			useTarget("${target.group}:kotlin-stdlib-jdk8:${target.version}")
			because("https://issuetracker.google.com/issues/112761162")
		}
	}
}

val props = Properties().apply {
	load(file("../gradle.properties").inputStream())
}
val VERSION_PLUGIN_QUALITY: String by props
val VERSION_PLUGIN_ANDROID: String by props
dependencies {
	compileOnly(gradleApi())
	implementation(kotlin("gradle-plugin"))

	configurations.all { resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.SECONDS) /* -SNAPSHOT */ }
	implementation("net.twisterrob.gradle:twister-quality:${VERSION_PLUGIN_QUALITY}")
	implementation("net.twisterrob.gradle:plugin:${VERSION_PLUGIN_ANDROID}")

	testImplementation("junit:junit:4.12")
	testImplementation("org.mockito:mockito-core:2.23.0")
	testImplementation(gradleApi())
}
