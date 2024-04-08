package io.github.kmbisset89.kmpbuildconfig.plugin

import io.github.kmbisset89.kmpbuildconfig.plugin.logic.ConfigProperty
import org.gradle.api.tasks.Nested

data class Config(

    @Nested
    val properties: List<ConfigProperty>,
)


