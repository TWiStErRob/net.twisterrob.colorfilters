plugins {
	`kotlin-dsl`
}

repositories {
	google()
	mavenCentral()
}

dependencies {
	compileOnly(gradleApi())
	implementation(libs.kotlin.gradle)
	implementation(libs.kotlin.detekt)

	configurations.all { resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.SECONDS) /* -SNAPSHOT */ }
	implementation(libs.android.gradle)
	implementation(libs.twisterrob.quality)
	implementation(libs.twisterrob.android)
	implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
	kotlinOptions {
		allWarningsAsErrors = true
	}
}
