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

val gradleVersion: String = GradleVersion.current().version

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

// TODEL Gradle sync in AS EE 2022.1.1 https://youtrack.jetbrains.com/issue/IDEA-301430, fixed in AS Giraffe.
if (System.getProperty("idea.version") ?: "" < "2022.3") {
	@Suppress("MaxLineLength")
	doNotNagAbout(
		"The org.gradle.util.GUtil type has been deprecated. " +
			"This is scheduled to be removed in Gradle 9.0. " +
			"Consult the upgrading guide for further information: " +
			"https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_7.html#org_gradle_util_reports_deprecations",
		"at org.jetbrains.plugins.gradle.tooling.builder.ExternalProjectBuilderImpl\$_getSourceSets_closure"
	)
} else {
	error("Android Studio version changed, please remove hack.")
}

// TODEL Gradle sync in AS EE 2022.1.1 https://youtrack.jetbrains.com/issue/IDEA-306975, maybe fixed in AS H.
@Suppress("MaxLineLength")
doNotNagAbout(
	"The AbstractArchiveTask.archivePath property has been deprecated. " +
		"This is scheduled to be removed in Gradle 9.0. " +
		"Please use the archiveFile property instead. " +
		"See https://docs.gradle.org/${gradleVersion}/dsl/org.gradle.api.tasks.bundling.AbstractArchiveTask.html#org.gradle.api.tasks.bundling.AbstractArchiveTask:archivePath for more details.",
	"at org.jetbrains.plugins.gradle.tooling.builder.ExternalProjectBuilderImpl\$_getSourceSets_closure"
)

// TODEL Gradle sync in AS EE 2022.1.1 https://youtrack.jetbrains.com/issue/IDEA-306975, maybe fixed in AS H.
@Suppress("MaxLineLength")
doNotNagAbout(
	"The AbstractArchiveTask.archivePath property has been deprecated. " +
		"This is scheduled to be removed in Gradle 9.0. " +
		"Please use the archiveFile property instead. " +
		"See https://docs.gradle.org/${gradleVersion}/dsl/org.gradle.api.tasks.bundling.AbstractArchiveTask.html#org.gradle.api.tasks.bundling.AbstractArchiveTask:archivePath for more details.",
	"at org.jetbrains.plugins.gradle.tooling.util.SourceSetCachedFinder.createArtifactsMap"
)
