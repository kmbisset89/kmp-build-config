package io.github.kmbisset89.kmpbuildconfig.plugin

import io.github.kmbisset89.kmpbuildconfig.plugin.logic.ConfigPropertiesBuilder
import io.github.kmbisset89.kmpbuildconfig.plugin.logic.ConfigProperty
import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.Input

open class ConfigBuilder{
    @Input
    val allProperties: MutableList<ConfigProperty> = mutableListOf()

    internal fun build(): Config {
        return Config(
            properties = allProperties,
        )
    }

    @Suppress("UNUSED")
    fun configProperties(action: Action<ConfigPropertiesBuilder>) {
        val builder = ConfigPropertiesBuilder {
            action.execute(this)
        }
        allProperties.addAll(builder.allConfigProperties)
    }
}
