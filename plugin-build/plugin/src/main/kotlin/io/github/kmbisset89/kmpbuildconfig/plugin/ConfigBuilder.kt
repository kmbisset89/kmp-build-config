package io.github.kmbisset89.kmpbuildconfig.plugin

import io.github.kmbisset89.kmpbuildconfig.plugin.logic.ConfigPropertiesBuilder
import io.github.kmbisset89.kmpbuildconfig.plugin.logic.ConfigProperty
import org.apache.tools.ant.taskdefs.Property
import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory

open class ConfigBuilder(
    objectFactory: ObjectFactory
) {

    @Input
    val packageName = objectFactory.property(String::class.java)

    @Input
    val objectName = objectFactory.property(String::class.java)


    @Input
    val allProperties: MutableList<ConfigProperty> = mutableListOf()

    internal fun build(): Config {
        return Config(
            packageName = packageName.get(),
            objectName = objectName.get(),
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
