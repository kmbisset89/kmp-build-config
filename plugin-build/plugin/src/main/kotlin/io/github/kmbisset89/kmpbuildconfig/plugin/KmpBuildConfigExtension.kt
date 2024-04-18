package io.github.kmbisset89.kmpbuildconfig.plugin

import io.github.kmbisset89.kmpbuildconfig.plugin.logic.ConfigPropertiesBuilder
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.tasks.Internal
import javax.inject.Inject


/**
 * Defines an extension for Kotlin Multiplatform projects to configure BuildConfig generation parameters.
 * This abstract class is used to extend project configurations in Gradle build scripts, allowing users to
 * specify settings for the BuildConfig file generation, such as the file name, package name, and source set.
 *
 * Note: This class is abstract and requires instantiation within the Gradle build environment, typically
 * as a Gradle extension.
 *
 * @param project The Gradle [Project] instance this extension is associated with. Used to access
 * project-specific features and properties.
 */
@Suppress("UnnecessaryAbstractClass")
abstract class KmpBuildConfigExtension(project: Project) {
    // Injection of the Project's ObjectFactory to create property instances.
    @Inject
    private val objects = project.objects

    // Property for specifying the name of the BuildConfig file to be generated.
    val buildConfigFileName = objects.property(String::class.java)

    // Property for specifying the name of the secret key file to be used in the generated BuildConfig file.
    val secretKeyFileName = objects.property(String::class.java)

    // Property for specifying the package name to be used in the generated BuildConfig file.
    val packageName = objects.property(String::class.java)

    // Property for specifying the source set to generate the BuildConfig file in.
    val sourceDir = objects.property(SourceDirectorySet::class.java)

    // Holds the configuration properties defined in the build script. Marked as Internal as it should
    // not be considered an input or output for task up-to-date checks.
    @Internal
    lateinit var config: ConfigProperties

    /**
     * Configures the properties for the BuildConfig file using a DSL defined in [ConfigPropertiesBuilder].
     * Allows the build script to define custom properties that will be included in the generated BuildConfig.
     *
     * @param action The configuration action to be applied to the [ConfigPropertiesBuilder], defining
     * the properties to include in the BuildConfig.
     */
    fun configProperties(action: Action<ConfigPropertiesBuilder>) {
        val builder = ConfigPropertiesBuilder {
            action.execute(this)
        }
        config = ConfigProperties(builder.allConfigProperties)
    }
}
