package io.github.kmbisset89.kmpbuildconfig.plugin.logic

import kotlin.reflect.full.createType

/**
 * Scope that can collect a list of [ConfigPropertyTypes] using a DSL.
 *
 * This is used both for the root `configProperties { ... }` block and for nested
 * `sourceSet("...") { ... }` blocks.
 */
open class SourceSetPropertiesBuilder(
    internal val allConfigProperties: MutableList<ConfigPropertyTypes>,
) {
    /**
     * Convenience alias so users can write `"key" to "value"` instead of `"key" withString "value"`.
     *
     * Note: This shadows Kotlin's stdlib `to` extension inside this DSL scope.
     */
    infix fun String.to(value: String) {
        this.withString(value)
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

    /**
     * Adds a complex object configuration property to the configuration.
     * This allows for nesting of properties within an object structure.
     *
     * @param valueBuilder A lambda function to define the nested properties within this object.
     */
    infix fun String.withObj(valueBuilder: SourceSetPropertiesBuilder.() -> Unit) {
        val builder = SourceSetPropertiesBuilder(mutableListOf()).apply(valueBuilder)
        allConfigProperties.add(
            ConfigPropertyTypes.ObjectConfigPropertyTypes(
                name = this,
                properties = builder.allConfigProperties.toList()
            )
        )
    }

    infix fun String.withSecretString(value: Pair<String, String>) {
        allConfigProperties.add(
            ConfigPropertyTypes.SecretConfigPropertyType(
                name = this,
                pair = value,
            )
        )
    }

    infix fun String.guardedBy(value: String): Pair<String, String> {
        return Pair(this, value)
    }
}

/**
 * Root builder for `configProperties { ... }`.
 *
 * Users can either add properties directly (defaults to [DEFAULT_SOURCE_SET_NAME]) or group them by source set:
 *
 * ```
 * configProperties {
 *   sourceSet("commonMain") { "foo" to "bar" }
 *   sourceSet("androidMain") { "baz" withString "qux" }
 * }
 * ```
 */
open class ConfigPropertiesBuilder(initBlock: ConfigPropertiesBuilder.() -> Unit) :
    SourceSetPropertiesBuilder(mutableListOf()) {

    internal companion object {
        const val DEFAULT_SOURCE_SET_NAME = "commonMain"
    }

    internal val sourceSetProperties: LinkedHashMap<String, MutableList<ConfigPropertyTypes>> = linkedMapOf()

    init {
        // Anything added directly to this builder is treated as `commonMain` for backward compatibility.
        sourceSetProperties[DEFAULT_SOURCE_SET_NAME] = allConfigProperties
        initBlock()
    }

    fun sourceSet(name: String, block: SourceSetPropertiesBuilder.() -> Unit) {
        val list = sourceSetProperties.getOrPut(name) { mutableListOf() }
        SourceSetPropertiesBuilder(list).apply(block)
    }
}
