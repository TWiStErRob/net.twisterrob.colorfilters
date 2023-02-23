package net.twisterrob.colorfilters.build

import com.android.build.gradle.internal.tasks.DexMergingTask

// Prevent the following error by allowing only a few parallel executions of these tasks:
// > com.android.builder.dexing.DexArchiveMergerException: Error while merging dex archives:
// > Caused by: java.lang.OutOfMemoryError: Java heap space
// > Expiring Daemon because JVM heap space is exhausted
@Suppress("MagicNumber")
val instances = (Runtime.getRuntime().maxMemory() / 1e9 - 1).toInt().coerceAtLeast(1)
registerLimitTasksService("dexMergingTaskLimiter", instances)
afterEvaluate { // To get numberOfBuckets populated.
	tasks.withType<DexMergingTask>().configureEach {
		if (numberOfBuckets.get() == 1) { // Implies DexMergingAction.MERGE_ALL|MERGE_EXTERNAL_LIBS.
			@Suppress("UnstableApiUsage")
			usesService(gradle.sharedServices.registrations.getAt("dexMergingTaskLimiter").service)
		}
	}
}

fun Project.registerLimitTasksService(name: String, maxInstances: Int) {
	abstract class TaskLimiter : BuildService<BuildServiceParameters.None>
	gradle.sharedServices.registerIfAbsent(name, TaskLimiter::class.java) {
		project.logger.info("Registering task limiter '$name' for max $maxInstances instances.")
		maxParallelUsages.set(maxInstances)
	}
}
