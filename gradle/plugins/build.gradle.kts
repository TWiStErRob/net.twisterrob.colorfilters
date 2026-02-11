plugins {
	`kotlin-dsl`
	id("java-gradle-plugin")
}

dependencies {
	compileOnly(gradleApi())
	implementation(libs.kotlin.gradle)
	implementation(libs.kotlin.detekt)
	implementation(libs.kotlin.kapt)

	configurations.all { resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.SECONDS) /* -SNAPSHOT */ }
	implementation(libs.android.gradle)
	implementation(libs.twisterrob.quality)
	implementation(libs.twisterrob.android)
	// TODEL https://github.com/gradle/gradle/issues/15383
	implementation(files(libs::class.java.superclass.protectionDomain.codeSource.location))
}

kotlin {
	compilerOptions {
		allWarningsAsErrors = true
	}
}

tasks.named("pluginDescriptors").configure {
	finalizedBy("validatePlugins")
}

tasks.withType<ValidatePlugins>().configureEach {
	ignoreFailures = false
	failOnWarning = true
	enableStricterValidation = true
}
