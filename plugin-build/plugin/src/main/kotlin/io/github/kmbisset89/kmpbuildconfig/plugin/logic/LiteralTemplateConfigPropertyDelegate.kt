package io.github.kmbisset89.kmpbuildconfig.plugin.logic

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

open class LiteralTemplateConfigPropertyDelegate<T>(
    val value: T,
    val template: String,
    val configPropertiesBuilder: ConfigPropertiesBuilder
) {
    operator fun provideDelegate(
        thisRef: Nothing?,
        prop: KProperty<*>
    ): ReadOnlyProperty<Nothing?, T> {
        val property = ConfigProperty.LiteralTemplateConfigProperty(
            name = prop.name,
            template = template,
            value = value,
            type = prop.returnType
        )
        configPropertiesBuilder.allConfigProperties.add(property)
        return ReadOnlyProperty { _, _ -> value }
    }
}
