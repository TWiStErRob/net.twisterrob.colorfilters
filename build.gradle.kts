plugins {
	id("net.twisterrob.gradle.plugin.root")
	id("net.twisterrob.gradle.plugin.quality")
	// https://developer.android.com/studio/test/command-line#multi-module-reports-instrumented-tests
	id("io.gitlab.arturbosch.detekt")
}

// Register root tasks before evaluating subprojects.
tasks.register<io.gitlab.arturbosch.detekt.report.ReportMergeTask>("detektReportMergeSarif") {
	output.set(rootProject.buildDir.resolve("reports/detekt/merge.sarif"))
}
tasks.register<io.gitlab.arturbosch.detekt.report.ReportMergeTask>("detektReportMergeXml") {
	output.set(rootProject.buildDir.resolve("reports/detekt/merge.xml"))
}

// TODEL https://issuetracker.google.com/issues/222730176
// This makes sure to pick up all subprojects not just direct children.
// com.android.build.gradle.internal.plugins.ReportingPlugin reads the subprojects in afterEvaluate,
// so this will run at the right time for it to observe evaluated children.
subprojects.forEach { evaluationDependsOn(it.path) } // evaluationDependsOnSubprojects()
apply(plugin = "android-reporting")

tasks.register<Delete>("clean") {
	delete(rootProject.buildDir)
}

tasks.register("check") {
	dependsOn(gradle.includedBuilds.map { it.task(":check") })
}
