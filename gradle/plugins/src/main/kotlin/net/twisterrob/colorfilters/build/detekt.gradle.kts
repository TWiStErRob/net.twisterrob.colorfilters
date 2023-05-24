package net.twisterrob.colorfilters.build

import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.report.ReportMergeTask
import net.twisterrob.colorfilters.build.dsl.libs

plugins {
	id("io.gitlab.arturbosch.detekt")
}

detekt {
	ignoreFailures = true
	buildUponDefaultConfig = true
	allRules = true
	config.setFrom(rootProject.file("config/detekt/detekt.yml"))
	baseline = rootProject.file("config/detekt/detekt-baseline-${project.name}.xml")
	basePath = rootProject.projectDir.absolutePath

	parallel = true

	tasks.withType<Detekt>().configureEach {
		// Target version of the generated JVM bytecode. It is used for type resolution.
		jvmTarget = libs.versions.java.get()
		reports {
			html.required.set(true) // human
			xml.required.set(true) // checkstyle
			txt.required.set(true) // console
			// https://sarifweb.azurewebsites.net
			sarif.required.set(true) // Github Code Scanning
		}
	}
}

val detektReportMergeSarif = rootProject.tasks.named<ReportMergeTask>("detektReportMergeSarif")
tasks.withType<Detekt>().configureEach {
	finalizedBy(detektReportMergeSarif)
}
detektReportMergeSarif.configure {
	input.from(tasks.withType<Detekt>().map { it.sarifReportFile })
}

val detektReportMergeXml = rootProject.tasks.named<ReportMergeTask>("detektReportMergeXml")
tasks.withType<Detekt>().configureEach {
	finalizedBy(detektReportMergeXml)
}
detektReportMergeXml.configure {
	input.from(tasks.withType<Detekt>().map { it.xmlReportFile })
}
