import org.gradle.accessors.dm.LibrariesForLibs.VersionAccessors
import org.gradle.api.JavaVersion

val VersionAccessors.javaVersion: JavaVersion
	get() = JavaVersion.toVersion(this.java.get())
