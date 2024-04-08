package io.github.kmbisset89.kmpbuildconfig.plugin.logic

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import io.github.kmbisset89.kmpbuildconfig.plugin.Config
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet
import java.io.File

/**
 * Generates a Kotlin file containing a BuildConfig object with compile-time constants.
 * This use case facilitates the dynamic generation of a BuildConfig class for a project,
 * allowing for easy access to version information and custom properties within the project's code.
 *
 * The generated BuildConfig file will be placed in the specified source set directory,
 * making it accessible to the project's source code.
 *
 * @constructor Creates an instance of the use case.
 */
class WriteBuildConfigFileUseCase {

    /**
     * Invokes the use case to generate the BuildConfig file.
     *
     * @param packageName The package name under which the BuildConfig file will be generated.
     * @param sourceSetName The name of the source set where the BuildConfig file will be placed.
     * @param buildConfigFileName The name of the BuildConfig file to be generated.

     */
    operator fun invoke(
        packageName: String,
        sourceSetName: SourceSet,
        buildConfigFileName: String,
        config: Config,
        project: Project
    ) {
        // Prepare the Kotlin file specification with the BuildConfig object
        val kotlinFileBuilder = FileSpec.builder(packageName, buildConfigFileName)

        // Create a BuildConfig object with the VERSION property and additional properties from propertyMap
        val buildConfigObject = TypeSpec.objectBuilder(buildConfigFileName.substringBeforeLast(".kt"))
            .addModifiers(KModifier.PUBLIC).also { type ->
                config.properties.forEach {
                    when (it) {
                        is ConfigProperty.LiteralTemplateConfigProperty<*> -> it.build(type)
                        is ConfigProperty.ObjectConfigProperty -> it.build(type, kotlinFileBuilder)
                    }
                }
            }

        val kotlinFile = kotlinFileBuilder.addType(buildConfigObject.build()).build()

        // Define the output directory for the Kotlin file within the specified source set
        val outputDir = project.file(
            buildString {
                appendFileSeparator
                append("build")
                appendFileSeparator
                append("generated")
                appendFileSeparator
                append("config")
                appendFileSeparator
                append(project.rootProject.name)
                appendFileSeparator
                append(project.name)
                appendFileSeparator
                append("src")
                appendFileSeparator
                append(sourceSetName.name)
                appendFileSeparator
                append("kotlin")
            }
        )

        // Ensure the output directory exists
        outputDir.mkdirs()

        // Write the generated Kotlin file to the output directory
        kotlinFile.writeTo(outputDir)

    }
}

private val StringBuilder.appendFileSeparator: StringBuilder
    get() = append(File.separator)
