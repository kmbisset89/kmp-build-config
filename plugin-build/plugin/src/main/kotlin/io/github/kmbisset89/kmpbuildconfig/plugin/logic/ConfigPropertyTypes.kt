package io.github.kmbisset89.kmpbuildconfig.plugin.logic

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional
import kotlin.reflect.KType

/**
 * Represents a configuration property within a Kotlin source file. It is the base class for more specific types of
 * configuration properties.
 */
sealed class ConfigPropertyTypes {

    /**
     * Represents a complex configuration property that consists of a nested object structure.
     *
     * @property name The name of the object, which should adhere to PascalCase naming conventions.
     * @property properties A list of [ConfigPropertyTypes] items representing the nested properties of this object.
     */
    open class ObjectConfigPropertyTypes(
        @Input
        val name: String,
        @Nested
        val properties: List<ConfigPropertyTypes>
    ) : ConfigPropertyTypes() {
        /**
         * Builds the nested object structure and adds it to the provided [TypeSpec.Builder] and [FileSpec.Builder].
         *
         * @param typeSpecBuilder The builder for constructing type specifications.
         * @param fileSpecBuilder The builder for constructing the file specification.
         */
        fun build(typeSpecBuilder: TypeSpec.Builder, fileSpecBuilder: FileSpec.Builder) {
            val type = TypeSpec.objectBuilder(name.fromCamelCaseToPascalCase()).also { builder ->
                properties.forEach {
                    when (it) {
                        is PrimitiveConfigPropertyTypes<*> -> it.build(builder)
                        is ObjectConfigPropertyTypes -> it.build(builder, fileSpecBuilder)
                        is SecretConfigPropertyType -> {
                           throw IllegalStateException("SecretConfigPropertyType should not be nested within another object.")
                        }
                    }
                }
            }.build()
            typeSpecBuilder.addType(type)
        }
    }

    /**
     * Represents a primitive configuration property.
     *
     * @property name The name of the property, which should adhere to snake_case naming conventions.
     * @property type The Kotlin type of the property.
     * @property template A string template used for initializing the property.
     * @property value The value to be assigned to the property.
     */
    open class PrimitiveConfigPropertyTypes<T>(
        @Input
        val name: String,
        @Internal
        val type: KType,
        @Input
        val template: String,
        @Input
        @Optional
        val value: T
    ) : ConfigPropertyTypes() {
        /**
         * Builds the primitive property and adds it to the provided [TypeSpec.Builder].
         *
         * @param typeSpecBuilder The builder for constructing type specifications.
         */
        fun build(typeSpecBuilder: TypeSpec.Builder) {
            val prop = PropertySpec
                .builder(name.fromCamelCaseToSnakeCase(), type.asTypeName())
                .initializer(template, value)
                .build()
            typeSpecBuilder.addProperty(prop)
        }
    }

    /**
     * Represents a secret configuration property that consists of an encrypted value and a key.
     *
     * @property name The base name of the property, which should adhere to snake_case naming conventions for the encrypted value.
     * @property pair A Pair where the first element is the encryption key and the second is the encrypted value.
     */
    class SecretConfigPropertyType(
        @Input
        val name: String,
        @Input
        val pair: Pair<String, String>
    ) : ConfigPropertyTypes() {
        /**
         * Builds the secret property and adds it to two different [TypeSpec.Builder] instances.
         *
         * @param encryptedValueBuilder The builder for constructing type specifications for the encrypted value.
         * @param encryptionKeyBuilder The builder for constructing type specifications for the encryption key.
         */
        fun build(encryptedValueBuilder: TypeSpec.Builder, encryptionKeyBuilder: TypeSpec.Builder) {
            // Assume the name is for the encrypted value, and append "Key" for the encryption key's name.
            val encryptedValueProp = PropertySpec.builder(name.fromCamelCaseToSnakeCase(), String::class)
                .initializer("%S", pair.first)
                .build()

            val encryptionKeyProp = PropertySpec.builder("${name.fromCamelCaseToSnakeCase()}_KEY", String::class)
                .initializer("%S", pair.second)
                .build()

            encryptedValueBuilder.addProperty(encryptedValueProp)
            encryptionKeyBuilder.addProperty(encryptionKeyProp)
        }
    }

    /**
     * Converts a camelCase string to snake_case.
     */
    internal fun String.fromCamelCaseToSnakeCase(): String {
        return this.replace(Regex("([a-z])([A-Z]+)"), "$1_$2").uppercase()
    }

    /**
     * Converts a camelCase string to PascalCase.
     */
    internal fun String.fromCamelCaseToPascalCase(): String {
        return this.replaceFirstChar { it.uppercase() }
    }
}
