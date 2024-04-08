package io.github.kmbisset89.kmpbuildconfig.plugin.logic

import kotlin.reflect.full.createType

open class ConfigPropertiesBuilder(initBlock: ConfigPropertiesBuilder.() -> Unit) {
    val allConfigProperties: MutableList<ConfigProperty> = mutableListOf()

    init {
        initBlock()
    }


    infix fun String.withOptionalString(value: String?) {
        val property = ConfigProperty.LiteralTemplateConfigProperty(
            name = this,
            template = "%S",
            value = value,
            type = String::class.createType(nullable = true)
        )
        allConfigProperties.add(property)
    }

    infix fun String.withString(value: String)  {
        val property = ConfigProperty.LiteralTemplateConfigProperty(
            name = this,
            template = "%S",
            value = value,
            type = String::class.createType(nullable = false)
        )
        allConfigProperties.add(property)
    }

    infix fun String.withOptionalBool(value: Boolean?) {
            val property = ConfigProperty.LiteralTemplateConfigProperty(
                name = this,
                template = "%L",
                value = value,
                type = Boolean::class.createType(nullable = true)
            )
            allConfigProperties.add(property)
        }

    infix fun String.withBool(value: Boolean) {
        val property = ConfigProperty.LiteralTemplateConfigProperty(
            name = this,
            template = "%L",
            value = value,
            type = Boolean::class.createType(nullable = false)
        )
        allConfigProperties.add(property)
    }

    infix fun String.withOptionalInt(value: Int?) {
        val property = ConfigProperty.LiteralTemplateConfigProperty(
            name = this,
            template = "%L",
            value = value,
            type = Int::class.createType(nullable = true)
        )
        allConfigProperties.add(property)
    }

    infix fun String.withInt(value: Int) {
        val property = ConfigProperty.LiteralTemplateConfigProperty(
            name = this,
            template = "%L",
            value = value,
            type = Int::class.createType(nullable = false)
        )
        allConfigProperties.add(property)
    }

    infix fun String.withOptionalLong(value: Long?) {
        val property = ConfigProperty.LiteralTemplateConfigProperty(
            name = this,
            template = "%L",
            value = value,
            type = Long::class.createType(nullable = true)
        )
        allConfigProperties.add(property)
    }

    infix fun String.withLong(value: Long) {
        val property = ConfigProperty.LiteralTemplateConfigProperty(
            name = this,
            template = "%L",
            value = value,
            type = Long::class.createType(nullable = false)
        )
        allConfigProperties.add(property)
    }

    infix fun String.withOptionalFloat(value: Float?) {
        val property = ConfigProperty.LiteralTemplateConfigProperty(
            name = this,
            template = "%L",
            value = value,
            type = Float::class.createType(nullable = true)
        )
        allConfigProperties.add(property)
    }

    infix fun String.withFloat(value: Float) {
        val property = ConfigProperty.LiteralTemplateConfigProperty(
            name = this,
            template = "%L",
            value = value,
            type = Float::class.createType(nullable = false)
        )
        allConfigProperties.add(property)
    }

    infix fun String.withOptionalDouble(value: Double?) {
        val property = ConfigProperty.LiteralTemplateConfigProperty(
            name = this,
            template = "%L",
            value = value,
            type = Double::class.createType(nullable = true)
        )
        allConfigProperties.add(property)
    }

    infix fun String.withDouble(value: Double) {
        val property = ConfigProperty.LiteralTemplateConfigProperty(
            name = this,
            template = "%L",
            value = value,
            type = Double::class.createType(nullable = false)
        )
        allConfigProperties.add(property)
    }

    infix fun String.withObj(valueBuilder: ConfigPropertiesBuilder.() -> Unit) {
        val builder = ConfigPropertiesBuilder(valueBuilder)
        val property = ConfigProperty.ObjectConfigProperty(
            name = this,
            properties = builder.allConfigProperties
        )
        allConfigProperties.add(property)
    }
}
