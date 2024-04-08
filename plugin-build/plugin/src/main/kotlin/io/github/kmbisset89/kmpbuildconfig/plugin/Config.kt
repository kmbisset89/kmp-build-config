package io.github.kmbisset89.kmpbuildconfig.plugin

import io.github.kmbisset89.kmpbuildconfig.plugin.logic.ConfigProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested

data class Config(
    @Input
    val packageName: String,
    @Input
    val objectName: String,
    @Nested
    val properties: List<ConfigProperty>,
)


