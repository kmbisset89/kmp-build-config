package io.github.kmbisset89.kmpbuildconfig.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * A Gradle plugin for Kotlin Multiplatform (KMP) projects that facilitates the creation of a BuildConfig file.
 * This plugin allows configuring version number, package name, source set name, build config file name,
 * and additional properties via an extension named `kmpBuildConfig`. It registers a task `createBuildConfig`
 * that generates a BuildConfig Kotlin file with the specified configurations.
 */
abstract class KmpBuildConfigPlugin : Plugin<Project> {
    /**
     * Applies the plugin to the given project. It sets up the plugin extension for configuration and registers
     * the `createBuildConfig` task, which generates the BuildConfig file based on the extension properties.
     *
     * @param project The project to which the plugin is applied.
     */
    override fun apply(project: Project) {
        // Create an extension for this plugin to allow configuration via the build script.
        val extension = project.extensions.create(EXTENSION_NAME, KmpBuildConfig::class.java, project)

        // Register the 'createBuildConfig' task and configure it with the properties defined in the extension.
        val releaseCandidateVersionTask =
            project.tasks.register(CREATE_BUILD_CONFIG, MakeBuildConfig::class.java) {
                it.version.set(extension.versionNumber) // Set the version number from the extension.
                it.packageName.set(extension.packageName) // Set the package name from the extension.
                it.propertyMap.set(extension.buildConfigProperties) // Set additional properties from the extension.
                it.sourceSetName.set(extension.sourceSetName) // Set the source set name from the extension, if specified.
                it.buildConfigFileName.set(extension.buildConfigFileName) // Set the build config file name from the extension, if specified.
            }
    }

    companion object {
        // Names for the extension and the task provided by this plugin.
        const val EXTENSION_NAME = "kmpBuildConfig"
        const val CREATE_BUILD_CONFIG = "createBuildConfig"
    }
}
