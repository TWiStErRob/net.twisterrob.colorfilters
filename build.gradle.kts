plugins {
	id("net.twisterrob.gradle.plugin.root")
	id("net.twisterrob.gradle.plugin.quality") apply false
	id("io.gitlab.arturbosch.detekt")
}

// TODEL when net.twisterrob.gradle.quality supports AGP 9.2.
// Preempt its GlobalLintGlobalFinalizerTask, which uses the removed LINT_XML_REPORT internal artifact.
tasks.register("lint") {
	dependsOn(subprojects.map { project -> project.tasks.matching { it.name == "lint" } })
}
apply(plugin = "net.twisterrob.gradle.plugin.quality")

// Register root tasks before evaluating subprojects.
tasks.register<io.gitlab.arturbosch.detekt.report.ReportMergeTask>("detektReportMergeSarif") {
	output = rootProject.layout.buildDirectory.file("reports/detekt/merge.sarif")
}
tasks.register<io.gitlab.arturbosch.detekt.report.ReportMergeTask>("detektReportMergeXml") {
	output = rootProject.layout.buildDirectory.file("reports/detekt/merge.xml")
}

tasks.register<Delete>("clean") {
	delete(rootProject.layout.buildDirectory)
}

tasks.register("check") {
	dependsOn(gradle.includedBuilds.map { it.task(":check") })
}
