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

val connectedCheck = tasks.register("connectedCheck") {
	group = LifecycleBasePlugin.VERIFICATION_GROUP
	description = "Runs connected device checks in all Android subprojects."
}
val mergeAndroidReports = tasks.register<Sync>("mergeAndroidReports") {
	group = LifecycleBasePlugin.VERIFICATION_GROUP
	description = "Generates the aggregated Android test report for CI."
	into(rootProject.layout.buildDirectory.dir("androidTest-results"))
}
subprojects {
	val androidProject = this
	plugins.withId("com.android.base") {
		connectedCheck.configure {
			dependsOn(androidProject.tasks.named("connectedCheck"))
		}
	}
	plugins.withId("com.android.application") {
		mergeAndroidReports.configure {
			dependsOn(androidProject.tasks.named("createAggregatedTestReport"))
			from(androidProject.layout.buildDirectory.dir("reports/tests/aggregated-test-report"))
		}
	}
}

tasks.register<Delete>("clean") {
	delete(rootProject.layout.buildDirectory)
}

tasks.register("check") {
	dependsOn(gradle.includedBuilds.map { it.task(":check") })
}
