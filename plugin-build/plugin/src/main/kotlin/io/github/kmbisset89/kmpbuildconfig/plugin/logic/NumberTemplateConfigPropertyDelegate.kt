package io.github.kmbisset89.kmpbuildconfig.plugin.logic

open class NumberTemplateConfigPropertyDelegate<T : Number?>(
    value: T,
    configPropertiesBuilder: ConfigPropertiesBuilder,
) : LiteralTemplateConfigPropertyDelegate<T>(
    value = value,
    template = "%L",
    configPropertiesBuilder = configPropertiesBuilder
)
