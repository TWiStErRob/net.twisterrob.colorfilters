import org.gradle.api.Project
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters

fun Project.registerLimitTasksService(name: String, maxInstances: Int) {
	abstract class TaskLimiter : BuildService<BuildServiceParameters.None>
	gradle.sharedServices.registerIfAbsent(name, TaskLimiter::class.java) {
		project.logger.info("Registering task limiter '$name' for max $maxInstances instances.")
		maxParallelUsages.set(maxInstances)
	}
}
