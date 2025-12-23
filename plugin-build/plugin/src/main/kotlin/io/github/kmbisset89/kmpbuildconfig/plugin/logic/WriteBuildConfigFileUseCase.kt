package io.github.kmbisset89.kmpbuildconfig.plugin.logic

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
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
        buildConfigTypeName: String,
        properties: List<ConfigPropertyTypes>,
        project: Project,
        secretKeyFileName: String?,
        secretKeyTypeName: String?,
        outputDir: File,
        writeCryptoUtils: Boolean,
    ) {
        val logger = project.logger

        // Prepare the Kotlin file specification with the BuildConfig object
        val kotlinFileBuilder = FileSpec.builder(packageName, buildConfigFileName)

        val kotlinSecretFileBuilder = secretKeyFileName?.let { FileSpec.builder(packageName, secretKeyFileName) }

        // Create a BuildConfig object with the VERSION property and additional properties from propertyMap
        val buildConfigObject = TypeSpec.objectBuilder(buildConfigTypeName)
            .addModifiers(KModifier.PUBLIC)

        val secretType = secretKeyFileName?.let {
            TypeSpec.objectBuilder(secretKeyTypeName ?: it.substringBeforeLast(".kt"))
                .addModifiers(KModifier.PUBLIC)
        }


        properties.forEach {
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

        // Define the CryptoUtils object (written once if requested)
        val encryptionUtil = if (writeCryptoUtils) {
            val cryptoUtils = TypeSpec.objectBuilder("CryptoUtils")
                .addFunction(decryptFunction())
                .build()
            FileSpec.builder(packageName, "CryptoUtils.kt")
                .addType(cryptoUtils)
                .build()
        } else {
            null
        }

        // Ensure the output directory exists
        outputDir.mkdirs()

        logger.info("Generated BuildConfig file: write to ${outputDir.path}")

        // Write the generated Kotlin file to the output directory
        kotlinFile.writeTo(outputDir)

        kotlinSecretFile?.writeTo(outputDir)

        encryptionUtil?.writeTo(outputDir)
    }


    // Function to define the decrypt method
    private fun decryptFunction(): FunSpec {
        val optIn = ClassName("kotlin", "OptIn")
        val experimentalEncodingApi = ClassName("kotlin.io.encoding", "ExperimentalEncodingApi")

        // Using addCode with a raw string to avoid any issues with % characters.
        return FunSpec.builder("decrypt")
            .addModifiers(KModifier.PUBLIC)
            .addAnnotation(
                AnnotationSpec.builder(optIn)
                    .addMember("%T::class", experimentalEncodingApi)
                    .build()
            )
            .addParameter("input", String::class)
            .addParameter("keyWord", String::class)
            .returns(String::class)
            .addCode(
                """
            fun legacyDecrypt(value: String, key: String): String {
                val shift = key.sumOf { it.code }.mod(26)
                return value.map { char ->
                    when (char) {
                        in 'A'..'Z' -> ('Z' - (('Z'.code - char.code + shift).mod(26))).toChar()
                        in 'a'..'z' -> ('z' - (('z'.code - char.code + shift).mod(26))).toChar()
                        else -> char
                    }
                }.joinToString("")
            }

            // New format (v1): kmbc1:<base64(nonce)>:<base64(ciphertext)>
            if (!input.startsWith("kmbc1:")) {
                return legacyDecrypt(input, keyWord)
            }

            return runCatching {
                val parts = input.split(':', limit = 3)
                if (parts.size != 3) error("Invalid encrypted payload format")

                val nonce = kotlin.io.encoding.Base64.Default.decode(parts[1])
                val cipherBytes = kotlin.io.encoding.Base64.Default.decode(parts[2])

                // FNV-1a 64-bit over (keyWord UTF-8 bytes) + 0x00 + nonce bytes.
                var hash = -0x340d631b7bdddcdbL // 14695981039346656037u as signed
                val prime = 0x100000001b3L

                for (b in keyWord.encodeToByteArray()) {
                    hash = hash xor (b.toLong() and 0xFFL)
                    hash *= prime
                }
                hash = hash xor 0x00L
                hash *= prime
                for (b in nonce) {
                    hash = hash xor (b.toLong() and 0xFFL)
                    hash *= prime
                }

                fun splitMix64(x: Long): Long {
                    val gamma = 0x9E3779B97F4A7C15uL.toLong()
                    val mul1 = 0xBF58476D1CE4E5B9uL.toLong()
                    val mul2 = 0x94D049BB133111EBuL.toLong()

                    var z = x + gamma
                    z = (z xor (z ushr 30)) * mul1
                    z = (z xor (z ushr 27)) * mul2
                    return z xor (z ushr 31)
                }

                // xoshiro256** (fast PRNG) seeded via splitmix64
                var seed = hash
                var s0 = splitMix64(seed).also { seed = it }
                var s1 = splitMix64(seed).also { seed = it }
                var s2 = splitMix64(seed).also { seed = it }
                var s3 = splitMix64(seed).also { seed = it }

                fun rotl(x: Long, k: Int): Long = (x shl k) or (x ushr (64 - k))

                fun nextLong(): Long {
                    val result = rotl(s1 * 5L, 7) * 9L
                    val t = s1 shl 17

                    s2 = s2 xor s0
                    s3 = s3 xor s1
                    s1 = s1 xor s2
                    s0 = s0 xor s3

                    s2 = s2 xor t
                    s3 = rotl(s3, 45)

                    return result
                }

                val plainBytes = ByteArray(cipherBytes.size)
                var ks = 0L
                var ksIdx = 8 // force initial refill
                for (i in cipherBytes.indices) {
                    if (ksIdx >= 8) {
                        ks = nextLong()
                        ksIdx = 0
                    }
                    val k = ((ks ushr (ksIdx * 8)) and 0xFFL).toInt()
                    plainBytes[i] = (cipherBytes[i].toInt() xor k).toByte()
                    ksIdx++
                }

                plainBytes.decodeToString()
            }.getOrElse {
                // If something goes wrong, fall back so older payloads (or user edits) don't crash runtime.
                legacyDecrypt(input, keyWord)
            }
        """.trimIndent()
            )
            .build()
    }
}

val StringBuilder.appendFileSeparator: StringBuilder
    get() = append(File.separator)
