package io.github.kmbisset89.kmpbuildconfig.plugin.logic

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import io.github.kmbisset89.kmpbuildconfig.plugin.ConfigProperties
import org.gradle.api.Project
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
     * @param buildConfigFileName The name of the BuildConfig file to be generated.

     */
    operator fun invoke(
        packageName: String,
        buildConfigFileName: String,
        config: ConfigProperties,
        project: Project,
        secretKeyFileName: String?
    ) {
        val logger = project.logger

        // Prepare the Kotlin file specification with the BuildConfig object
        val kotlinFileBuilder = FileSpec.builder(packageName, buildConfigFileName)

        val kotlinSecretFileBuilder = secretKeyFileName?.let { FileSpec.builder(packageName, secretKeyFileName) }

        // Create a BuildConfig object with the VERSION property and additional properties from propertyMap
        val buildConfigObject = TypeSpec.objectBuilder(buildConfigFileName.substringBeforeLast(".kt"))
            .addModifiers(KModifier.PUBLIC).also { type ->
                config.properties.forEach {
                    when (it) {
                        is ConfigPropertyTypes.PrimitiveConfigPropertyTypes<*> -> it.build(type)
                        is ConfigPropertyTypes.ObjectConfigPropertyTypes -> it.build(type, kotlinFileBuilder)
                        is ConfigPropertyTypes.SecretConfigPropertyType -> {
                            secretKeyFileName?.let { name  ->
                                TypeSpec.objectBuilder(name.substringBeforeLast(".kt"))
                                    .addModifiers(KModifier.PUBLIC).also { secretType ->
                                        it.build(type, secretType)
                                    }
                            } ?: throw IllegalStateException("Secret file name is needed if there are secret objects.")
                        }
                    }
                }
            }

        val kotlinFile = kotlinFileBuilder.addType(buildConfigObject.build()).build()

        val kotlinSecretFile = kotlinSecretFileBuilder?.addType(buildConfigObject.build())?.build()

        val directory = buildString {
            append("generated")
            appendFileSeparator
            append("source")
            appendFileSeparator
            append("buildConfig")
        }

        // Define the output directory for the Kotlin file within the specified source set
        val outputDir = project.layout.buildDirectory.dir(directory)

        // Ensure the output directory exists
        outputDir.get().asFile.mkdirs()

        logger.info("Generated BuildConfig file: write to ${outputDir.get().asFile.path}")

        // Write the generated Kotlin file to the output directory
        kotlinFile.writeTo(outputDir.get().asFile)

        kotlinSecretFile?.writeTo(outputDir.get().asFile)
    }
}

private val StringBuilder.appendFileSeparator: StringBuilder
    get() = append(File.separator)
