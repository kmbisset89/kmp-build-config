package io.github.kmbisset89.kmpbuildconfig.plugin

import io.github.kmbisset89.kmpbuildconfig.plugin.logic.ConfigPropertyTypes
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested

/**
 * Represents a collection of configuration properties for a specific purpose or module.
 * It's designed to encapsulate various types of configuration properties in a unified structure.
 *
 * @property sourceSets A list of source-set scoped configuration blocks.
 */
data class ConfigProperties(
    @Nested
    val sourceSets: List<SourceSetConfigProperties>,
)

/**
 * Configuration properties scoped to a single Kotlin source set (e.g. `commonMain`, `androidMain`).
 */
data class SourceSetConfigProperties(
    @Input
    val name: String,
    @Nested
    val properties: List<ConfigPropertyTypes>,
)
