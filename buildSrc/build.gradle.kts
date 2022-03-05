@file:Suppress("PropertyName")

import java.util.Properties

plugins {
	kotlin("jvm") version "1.4.32"
}
repositories {
	google()
	mavenCentral()
	maven { name = "ajoberstar-backup"; setUrl("https://ajoberstar.org/bintray-backup/") }
	maven { setUrl("http://localhost/maven") }
	maven { name = "Gradle libs (for Kotlin-DSL)"; setUrl("https://repo.gradle.org/gradle/libs-releases-local/") }
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
val VERSION_MOCKITO: String by props
val VERSION_JUNIT: String by props

dependencies {
	compileOnly(gradleApi())
	implementation(kotlin("gradle-plugin"))

	configurations.all { resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.SECONDS) /* -SNAPSHOT */ }
	implementation("net.twisterrob.gradle:twister-quality:${VERSION_PLUGIN_QUALITY}")
	implementation("net.twisterrob.gradle:plugin:${VERSION_PLUGIN_ANDROID}")
	// Prevent https://sourceforge.net/p/proguard/bugs/712/ in ProGuard 6.0.x (default in AGP 3.4)
	// Alternative: `-keep class module-info` and/or `-dontobfuscate` in proguard.pro
	implementation("net.sf.proguard:proguard-gradle:6.2.2")

	testImplementation("junit:junit:${VERSION_JUNIT}")
	testImplementation("org.mockito:mockito-core:${VERSION_MOCKITO}")
	testImplementation(gradleApi())
}
