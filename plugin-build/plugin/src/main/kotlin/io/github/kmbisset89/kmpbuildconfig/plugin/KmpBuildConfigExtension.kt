package io.github.kmbisset89.kmpbuildconfig.plugin

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.tasks.Internal
import javax.inject.Inject


@Suppress("UnnecessaryAbstractClass")
abstract class KmpBuildConfigExtension constructor(project: Project) {
    @Inject
    private val objects = project.objects

    val buildConfigFileName = objects.property(String::class.java)

    val sourceSetName = objects.property(String::class.java)

    val packageName = objects.property(String::class.java)

    @Internal
    lateinit var config: Config
    fun config(action: Action<ConfigBuilder>) {
        val builder = ConfigBuilder(objects)
        action.execute(builder)
        config = builder.build()
    }
}
