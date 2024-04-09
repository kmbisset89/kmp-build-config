package io.github.kmbisset89.kmpbuildconfig.plugin.logic

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
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

        // Define the CryptoUtils object
        val cryptoUtils = TypeSpec.objectBuilder("CryptoUtils")
            .addFunction(decryptFunction())
            .build()

        val encryptionUtil = secretKeyFileName?.let {
            FileSpec.builder(packageName, "CryptoUtils.kt")
                .addType(cryptoUtils)
                .build()
        }

        // Create a BuildConfig object with the VERSION property and additional properties from propertyMap
        val buildConfigObject = TypeSpec.objectBuilder(buildConfigFileName.substringBeforeLast(".kt"))
            .addModifiers(KModifier.PUBLIC)

        val secretType = secretKeyFileName?.let {
            TypeSpec.objectBuilder(it.substringBeforeLast(".kt"))
                .addModifiers(KModifier.PUBLIC)
        }


        config.properties.forEach {
            when (it) {
                is ConfigPropertyTypes.PrimitiveConfigPropertyTypes<*> -> it.build(buildConfigObject)
                is ConfigPropertyTypes.ObjectConfigPropertyTypes -> it.build(buildConfigObject, kotlinFileBuilder)
                is ConfigPropertyTypes.SecretConfigPropertyType -> {
                    secretType?.let { type ->
                        it.build(buildConfigObject, type)
                    } ?: throw IllegalStateException("Secret file name is needed if there are secret objects.")
                }
            }
        }


        val kotlinFile = kotlinFileBuilder.addType(buildConfigObject.build()).build()

        val kotlinSecretFile = secretType?.let { kotlinSecretFileBuilder?.addType(it.build())?.build()}

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

        encryptionUtil?.writeTo(outputDir.get().asFile)
    }


    // Function to define the decrypt method
    private fun decryptFunction(): FunSpec {
        // Using addCode with a raw string to avoid any issues with % characters.
        return FunSpec.builder("decrypt")
            .addModifiers(KModifier.PUBLIC)
            .addParameter("input", String::class)
            .addParameter("keyWord", String::class)
            .returns(String::class)
            .addCode(
                """
            val shift = keyWord.sumOf { it.code }.mod(26) // Calculate shift using .mod
            return input.map { char ->
                when (char) {
                    in 'A'..'Z' -> ('Z' - (('Z'.code - char.code + shift).mod(26))).toChar()
                    in 'a'..'z' -> ('z' - (('z'.code - char.code + shift).mod(26))).toChar()
                    else -> char
                }
            }.joinToString("")
        """.trimIndent()
            )
            .build()
    }
}

private val StringBuilder.appendFileSeparator: StringBuilder
    get() = append(File.separator)
