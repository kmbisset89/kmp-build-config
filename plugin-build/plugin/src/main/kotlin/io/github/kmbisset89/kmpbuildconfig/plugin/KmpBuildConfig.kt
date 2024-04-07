package io.github.kmbisset89.kmpbuildconfig.plugin

import org.gradle.api.Project
import org.gradle.api.provider.Property
import javax.inject.Inject


@Suppress("UnnecessaryAbstractClass")
abstract class KmpBuildConfig constructor(project: Project) {
    @Inject
    private val objects = project.objects

    val versionNumber = objects.property(String::class.java)

    val buildConfigProperties = objects.mapProperty(String::class.java, String::class.java)

    val buildConfigFileName = objects.property(String::class.java)

    val sourceSetName = objects.property(String::class.java)

    val packageName = objects.property(String::class.java)
}
