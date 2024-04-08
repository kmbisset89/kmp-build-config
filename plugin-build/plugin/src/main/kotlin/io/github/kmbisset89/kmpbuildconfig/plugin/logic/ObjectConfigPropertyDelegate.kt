package io.github.kmbisset89.kmpbuildconfig.plugin.logic

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

open class ObjectConfigPropertyDelegate(
    private val valueBuilder: ConfigPropertiesBuilder.() -> Unit,
    private val configPropertiesBuilder: ConfigPropertiesBuilder,
) {
    operator fun provideDelegate(
        thisRef: Nothing?,
        prop: KProperty<*>
    ): ReadOnlyProperty<Nothing?, ConfigProperty.ObjectConfigProperty> {
        val builder = ConfigPropertiesBuilder(valueBuilder)
        val property = ConfigProperty.ObjectConfigProperty(
            name = prop.name,
            properties = builder.allConfigProperties
        )
        configPropertiesBuilder.allConfigProperties.add(property)
        return ReadOnlyProperty { _, _ -> property }
    }
}
