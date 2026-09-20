plugins {
	id("net.twisterrob.gradle.plugin.root")
	id("net.twisterrob.gradle.plugin.quality")
	id("dev.detekt")
}

// Register root tasks before evaluating subprojects.
tasks.register<dev.detekt.gradle.report.ReportMergeTask>("detektReportMergeSarif") {
	output = rootProject.layout.buildDirectory.file("reports/detekt/merge.sarif")
}
tasks.register<dev.detekt.gradle.report.ReportMergeTask>("detektReportMergeXml") {
	output = rootProject.layout.buildDirectory.file("reports/detekt/merge.xml")
}

tasks.register<Delete>("clean") {
	delete(rootProject.layout.buildDirectory)
}

tasks.register("check") {
	dependsOn(gradle.includedBuilds.map { it.task(":check") })
}
