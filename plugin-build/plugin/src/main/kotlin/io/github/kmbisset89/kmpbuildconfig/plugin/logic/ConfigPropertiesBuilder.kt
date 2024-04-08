package io.github.kmbisset89.kmpbuildconfig.plugin.logic

import kotlin.reflect.full.createType

open class ConfigPropertiesBuilder(initBlock: ConfigPropertiesBuilder.() -> Unit) {
    val allConfigProperties: MutableList<ConfigProperty> = mutableListOf()

    init {
        initBlock()
    }


    infix fun String.withString(value: String?) = this@ConfigPropertiesBuilder.string(value).also {
        val property = ConfigProperty.LiteralTemplateConfigProperty(
            name = this,
            template = "%S",
            value = value,
            type = String::class.createType(nullable = true)
        )
        allConfigProperties.add(property)
    }


    fun <T : String?> string(value: T) = LiteralTemplateConfigPropertyDelegate(
        value = value,
        template = "%S",
        configPropertiesBuilder = this
    )

    fun <T : Boolean?> bool(value: T) = LiteralTemplateConfigPropertyDelegate(
        value = value,
        template = "%L",
        configPropertiesBuilder = this
    )

    @Suppress("UNUSED")
    fun <T : Boolean?> boolean(value: T) = bool(value)

    fun <T : Number?> number(value: T) = NumberTemplateConfigPropertyDelegate(
        value = value,
        configPropertiesBuilder = this
    )

    @Suppress("UNUSED")
    fun <T : Int?> int(value: T) = number(value)

    @Suppress("UNUSED")
    fun <T : Long?> long(value: T) = number(value)

    @Suppress("UNUSED")
    fun <T : Double?> double(value: T) = number(value)

    @Suppress("UNUSED")
    fun <T : Float?> float(value: Float?) = number(value)

    @Suppress("UNUSED")
    fun obj(valueBuilder: ConfigPropertiesBuilder.() -> Unit) =
        ObjectConfigPropertyDelegate(valueBuilder, this)
}
