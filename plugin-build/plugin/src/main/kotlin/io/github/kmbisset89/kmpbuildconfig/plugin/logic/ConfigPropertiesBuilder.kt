package io.github.kmbisset89.kmpbuildconfig.plugin.logic

import kotlin.reflect.full.createType

/**
 * A builder class for constructing configuration properties in a DSL-style manner.
 * Allows for easy creation and grouping of both primitive and object configuration properties.
 *
 * @param initBlock A lambda function to initialize the builder with the configuration properties.
 */
open class ConfigPropertiesBuilder(initBlock: ConfigPropertiesBuilder.() -> Unit) {
    // Holds all configuration properties added to this builder.
    val allConfigProperties: MutableList<ConfigPropertyTypes> = mutableListOf()

    // Initialize the builder with the provided block of configuration properties.
    init {
        initBlock()
    }

    /**
     * Adds an optional string property to the configuration.
     *
     * @param value The optional value for this property.
     */
    infix fun String.withOptionalString(value: String?) {
        allConfigProperties.add(
            ConfigPropertyTypes.PrimitiveConfigPropertyTypes(
                name = this,
                template = "%S",
                value = value,
                type = String::class.createType(nullable = true)
            )
        )
    }

    /**
     * Adds a mandatory string property to the configuration.
     *
     * @param value The value for this property.
     */
    infix fun String.withString(value: String) {
        allConfigProperties.add(
            ConfigPropertyTypes.PrimitiveConfigPropertyTypes(
                name = this,
                template = "%S",
                value = value,
                type = String::class.createType(nullable = false)
            )
        )
    }

    /**
     * Adds an optional boolean property to the configuration.
     *
     * @param value The optional value for this property.
     */
    infix fun String.withOptionalBool(value: Boolean?) {
        allConfigProperties.add(
            ConfigPropertyTypes.PrimitiveConfigPropertyTypes(
                name = this,
                template = "%L",
                value = value,
                type = Boolean::class.createType(nullable = true)
            )
        )
    }

    /**
     * Adds a mandatory boolean property to the configuration.
     *
     * @param value The value for this property.
     */
    infix fun String.withBool(value: Boolean) {
        allConfigProperties.add(
            ConfigPropertyTypes.PrimitiveConfigPropertyTypes(
                name = this,
                template = "%L",
                value = value,
                type = Boolean::class.createType(nullable = false)
            )
        )
    }

    /**
     * Adds an optional integer property to the configuration.
     *
     * @param value The optional value for this property.
     */
    infix fun String.withOptionalInt(value: Int?) {
        allConfigProperties.add(
            ConfigPropertyTypes.PrimitiveConfigPropertyTypes(
                name = this,
                template = "%L",
                value = value,
                type = Int::class.createType(nullable = true)
            )
        )
    }

    /**
     * Adds a mandatory integer property to the configuration.
     *
     * @param value The value for this property.
     */
    infix fun String.withInt(value: Int) {
        allConfigProperties.add(
            ConfigPropertyTypes.PrimitiveConfigPropertyTypes(
                name = this,
                template = "%L",
                value = value,
                type = Int::class.createType(nullable = false)
            )
        )
    }

    // Similar pattern is followed for other data types like Long, Float, and Double
    // and their nullable counterparts.

    /**
     * Adds a complex object configuration property to the configuration.
     * This allows for nesting of properties within an object structure.
     *
     * @param valueBuilder A lambda function to define the nested properties within this object.
     */
    infix fun String.withObj(valueBuilder: ConfigPropertiesBuilder.() -> Unit) {
        val builder = ConfigPropertiesBuilder(valueBuilder)
        allConfigProperties.add(
            ConfigPropertyTypes.ObjectConfigPropertyTypes(
                name = this,
                properties = builder.allConfigProperties
            )
        )
    }
}
