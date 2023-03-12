plugins {
	`kotlin-dsl`
	id("java-gradle-plugin")
}

dependencies {
	compileOnly(gradleApi())
	implementation(libs.kotlin.gradle)
	implementation(libs.kotlin.detekt)

	configurations.all { resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.SECONDS) /* -SNAPSHOT */ }
	implementation(libs.android.gradle)
	implementation(libs.twisterrob.quality)
	implementation(libs.twisterrob.android)
	// TODEL https://github.com/gradle/gradle/issues/15383
	implementation(files(libs::class.java.superclass.protectionDomain.codeSource.location))
}

gradlePlugin {
	// REPORT Why do I need to do this? It's already in the classpath.
	// Plus with this I get this warning: from Task :plugins:jar
	// > :jar: A valid plugin descriptor was found for net.twisterrob.settings.properties but the
	// > implementation class net.twisterrob.gradle.settings.SettingsPlugin was not found in the jar.
	plugins.register("exposedSettings") {
		id = "net.twisterrob.gradle.plugin.settings"
		implementationClass = "net.twisterrob.gradle.settings.SettingsPlugin"
	}
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
	compilerOptions {
		allWarningsAsErrors.set(true)
	}
}

tasks.named("pluginDescriptors").configure {
	finalizedBy("validatePlugins")
}

tasks.withType<ValidatePlugins>().configureEach {
	ignoreFailures.set(false)
	failOnWarning.set(true)
	enableStricterValidation.set(true)
}
