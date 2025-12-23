package io.github.kmbisset89.kmpbuildconfig.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

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
        val extension = project.extensions.create(EXTENSION_NAME, KmpBuildConfigExtension::class.java, project)

        // Register the 'createBuildConfig' task and configure it with the properties defined in the extension.
        val task =
            project.tasks.register(CREATE_BUILD_CONFIG, MakeBuildConfig::class.java) {
                it.packageName.set(extension.packageName) // Set the package name from the extension.
                it.buildConfigFileName.set(extension.buildConfigFileName) // Set the build config file name from the extension, if specified.
                it.secretKeyFileName.set(extension.secretKeyFileName) // Set the secret key file name from the extension, if specified.
                it.config = extension.config // Set the config object from the extension.
            }


        project.afterEvaluate {
            val kotlin = project.extensions.findByType(KotlinMultiplatformExtension::class.java)
                ?: throw IllegalStateException("KMP BuildConfig plugin requires the Kotlin Multiplatform plugin to be applied.")

            // Wire generated sources into each configured source set.
            extension.config.sourceSets.forEach { ss ->
                val outputDir = project.layout.buildDirectory
                    .dir("generated/source/buildConfig/${ss.name}")
                    .get()
                    .asFile

                val kotlinSourceSet = kotlin.sourceSets.findByName(ss.name)
                    ?: throw IllegalStateException("Kotlin sourceSet '${ss.name}' was not found.")

                kotlinSourceSet.kotlin.srcDir(outputDir)
            }

            // Ensure wiring runs after generation when users hook createBuildConfig into their build.
            // (Adding srcDirs is safe even if the directory doesn't exist yet.)
            task.get()
        }


    }

    companion object {
        // Names for the extension and the task provided by this plugin.
        const val EXTENSION_NAME = "kmpBuildConfig"
        const val CREATE_BUILD_CONFIG = "createBuildConfig"
    }
}
