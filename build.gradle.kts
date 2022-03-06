plugins {
	id("net.twisterrob.root")
	id("net.twisterrob.quality")
	// https://developer.android.com/studio/test/command-line#multi-module-reports-instrumented-tests
	id("android-reporting")
	id("io.gitlab.arturbosch.detekt")
}

// TODEL https://issuetracker.google.com/issues/222730176
// This makes sure to pick up all subprojects not just direct children.
// com.android.build.gradle.internal.plugins.ReportingPlugin reads the subprojects in afterEvaluate,
// so this will run at the right time for it to observe evaluated children.
subprojects.forEach { evaluationDependsOn(it.path) } // evaluationDependsOnSubprojects()

tasks.register<Delete>("clean") {
	delete(rootProject.buildDir)
}
