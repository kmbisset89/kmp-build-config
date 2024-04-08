package io.github.kmbisset89.kmpbuildconfig.plugin.logic

import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

class AddToSourceDirUseCase {
    operator fun invoke(project : Project, sourceSetName :String, srcDirToAdd : String) {

        project.extensions.findByType(KotlinSourceSet::class.java)?.let { kotlinSourceSet ->
            println("Found KotlinSourceSet")
            if (kotlinSourceSet.name == sourceSetName) {
                kotlinSourceSet.kotlin.srcDir(srcDirToAdd)
            }
        }
    }
}
