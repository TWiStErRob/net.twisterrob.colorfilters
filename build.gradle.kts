import net.twisterrob.colorfilters.build.dsl.isCI
import net.twisterrob.gradle.doNotNagAbout

plugins {
	id("net.twisterrob.gradle.plugin.root")
	id("net.twisterrob.gradle.plugin.quality")
	id("io.gitlab.arturbosch.detekt")
}

// Register root tasks before evaluating subprojects.
tasks.register<io.gitlab.arturbosch.detekt.report.ReportMergeTask>("detektReportMergeSarif") {
	output.set(rootProject.layout.buildDirectory.file("reports/detekt/merge.sarif"))
}
tasks.register<io.gitlab.arturbosch.detekt.report.ReportMergeTask>("detektReportMergeXml") {
	output.set(rootProject.layout.buildDirectory.file("reports/detekt/merge.xml"))
}

// TODEL https://issuetracker.google.com/issues/222730176
// This makes sure to pick up all subprojects not just direct children.
// com.android.build.gradle.internal.plugins.ReportingPlugin reads the subprojects in afterEvaluate,
// so this will run at the right time for it to observe evaluated children.
subprojects.forEach { evaluationDependsOn(it.path) } // evaluationDependsOnSubprojects()
// https://developer.android.com/studio/test/command-line#multi-module-reports-instrumented-tests
apply(plugin = "android-reporting")

tasks.register<Delete>("clean") {
	delete(rootProject.layout.buildDirectory)
}

tasks.register("check") {
	dependsOn(gradle.includedBuilds.map { it.task(":check") })
}

val gradleVersion: String = GradleVersion.current().version

// TODEL KGP 1.9.23 build on Gradle 8.8-rc-1 https://youtrack.jetbrains.com/issue/KT-67838 target fix 2.0.20.
val kgpVersion = libs.kotlin.gradle.get().version
@Suppress("MaxLineLength")
if (kgpVersion == "1.9.24") {
	doNotNagAbout(
		"The Configuration.fileCollection(Spec) method has been deprecated. " +
			"This is scheduled to be removed in Gradle 9.0. " +
			"Use Configuration.getIncoming().artifactView(Action) with a componentFilter instead. " +
			"Consult the upgrading guide for further information: " +
			"https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_8.html#deprecate_filtered_configuration_file_and_filecollection_methods",
		"at org.jetbrains.kotlin.gradle.internal.Kapt3GradleSubplugin\$createKaptKotlinTask\$2.invoke(Kapt3KotlinGradleSubplugin.kt:422)"
	)
} else {
	val error: (String) -> Unit = (if (isCI) ::error else logger::warn)
	error("KGP version (${kgpVersion}) changed, please review hack.")
}
