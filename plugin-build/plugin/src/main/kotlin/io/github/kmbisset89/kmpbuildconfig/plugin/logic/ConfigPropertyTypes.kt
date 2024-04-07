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
