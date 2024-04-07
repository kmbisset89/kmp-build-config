package io.github.kmbisset89.kmpbuildconfig.plugin

import io.github.kmbisset89.kmpbuildconfig.plugin.logic.ConfigPropertyTypes
import org.gradle.api.tasks.Nested

/**
 * Represents a collection of configuration properties for a specific purpose or module.
 * It's designed to encapsulate various types of configuration properties in a unified structure.
 *
 * @property properties A list of configuration property types. Each entry in this list is an instance
 * of [ConfigPropertyTypes], which can represent different kinds of configuration values, including
 * primitive types, complex object types, and lists. This allows for a flexible and extensible design
 * where different kinds of configuration properties can be added as needed.
 */
data class ConfigProperties(

    // Use the @Nested annotation to indicate that properties is a composite property
    // that should be treated as an embeddable list of properties, each with their own sub-properties
    // and types.
    @Nested
    val properties: List<ConfigPropertyTypes>,
)
