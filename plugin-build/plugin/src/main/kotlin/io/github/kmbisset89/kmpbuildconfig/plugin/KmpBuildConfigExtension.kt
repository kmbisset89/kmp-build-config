package io.github.kmbisset89.kmpbuildconfig.plugin

import io.github.kmbisset89.kmpbuildconfig.plugin.logic.ConfigPropertiesBuilder
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.tasks.Internal
import javax.inject.Inject


@Suppress("UnnecessaryAbstractClass")
abstract class KmpBuildConfigExtension(project: Project) {
    @Inject
    private val objects = project.objects

    val buildConfigFileName = objects.property(String::class.java)

    val sourceSetName = objects.property(String::class.java)

    val packageName = objects.property(String::class.java)

    @Internal
    lateinit var config: Config
    fun configProperties(action: Action<ConfigPropertiesBuilder>) {
        val builder = ConfigPropertiesBuilder {
            action.execute(this)
        }
        config = ConfigBuilder().apply {
            allProperties.addAll(builder.allConfigProperties)
        }.build()
    }
}
