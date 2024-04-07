package io.github.kmbisset89.kmpbuildconfig.plugin.logic

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import org.gradle.api.Project

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
     * @param version The application's version, which will be included in the BuildConfig.
     * @param packageName The package name under which the BuildConfig file will be generated.
     * @param sourceSetName The name of the source set where the BuildConfig file will be placed.
     * @param buildConfigFileName The name of the BuildConfig file to be generated.
     * @param propertyMap A map of additional properties to include in the BuildConfig object.
     *                    Each entry in the map represents a constant that will be added to BuildConfig.
     * @param project The Gradle project reference, used to locate the source set directory.
     */
    operator fun invoke(
        version: String,
        packageName: String,
        sourceSetName: String,
        buildConfigFileName: String,
        propertyMap: Map<String, String>,
        project: Project
    ) {
        // Create a BuildConfig object with the VERSION property and additional properties from propertyMap
        val buildConfigObject = TypeSpec.objectBuilder("BuildConfig")
            .addModifiers(KModifier.PUBLIC)
            .addProperty(
                PropertySpec.builder("VERSION", String::class, KModifier.CONST)
                    .initializer("%S", version)
                    .build())

        // Iterate through the propertyMap to add each property as a constant to BuildConfig
        propertyMap.forEach { (key, value) ->
            buildConfigObject.addProperty(
                PropertySpec.builder(key.uppercase(), String::class, KModifier.CONST)
                    .initializer("%S", value)
                    .build()
            )
        }

        // Prepare the Kotlin file specification with the BuildConfig object
        val kotlinFile = FileSpec.builder(packageName, buildConfigFileName)
            .addType(buildConfigObject.build())
            .build()

        // Define the output directory for the Kotlin file within the specified source set
        val outputDir = project.file("src/$sourceSetName/kotlin")

        // Ensure the output directory exists
        outputDir.mkdirs()

        // Write the generated Kotlin file to the output directory
        kotlinFile.writeTo(outputDir)
    }
}
