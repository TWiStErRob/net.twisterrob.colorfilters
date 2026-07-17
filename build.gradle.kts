plugins {
	id("net.twisterrob.gradle.plugin.root")
	id("net.twisterrob.gradle.plugin.quality")
	id("io.gitlab.arturbosch.detekt")
}

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
