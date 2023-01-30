import net.twisterrob.gradle.doNotNagAbout

plugins {
	id("net.twisterrob.root")
	id("net.twisterrob.quality")
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

val gradleVersion: String = GradleVersion.current().baseVersion.version

// TODEL https://issuetracker.google.com/issues/264177800
if (com.android.Version.ANDROID_GRADLE_PLUGIN_VERSION < "7.4.1") {
	@Suppress("MaxLineLength")
	doNotNagAbout(
		"The Report.destination property has been deprecated. " +
			"This is scheduled to be removed in Gradle 9.0. " +
			"Please use the outputLocation property instead. " +
			"See https://docs.gradle.org/${gradleVersion}/dsl/org.gradle.api.reporting.Report.html#org.gradle.api.reporting.Report:destination for more details.",
		"at com.android.build.gradle.tasks.factory.AndroidUnitTest\$CreationAction.configure"
	)
} else {
	error("AGP version changed, please remove hack.")
}
