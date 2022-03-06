plugins {
	id("net.twisterrob.kotlin")
	id("net.twisterrob.quality")
}

tasks.withType<JavaCompile> {
	options.compilerArgs = options.compilerArgs + "-Xlint:all"
	options.compilerArgs = options.compilerArgs + "-Werror"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
	kotlinOptions {
		allWarningsAsErrors = true
	}
}
