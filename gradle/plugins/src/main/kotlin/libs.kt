// TODEL https://github.com/gradle/gradle/issues/15383
// Not in package, because it's used everywhere and it would be expected to exist from kotlin-dsl.

import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

val Project.libs: LibrariesForLibs
	get() = this.extensions.getByType()
