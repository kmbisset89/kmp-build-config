package io.github.kmbisset89.kmpbuildconfig.plugin.logic

import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

class AddToSourceDirUseCase {
    operator fun invoke(project : Project, sourceSetName :String, srcDirToAdd : String) {

        val sourceSets = project.extensions.getByName("sourceSets") as SourceSetContainer
        println(sourceSets.joinToString { it.name })
        val commonMain = sourceSets.getByName("commonMain")
        (commonMain as? KotlinSourceSet)?.kotlin?.srcDir(srcDirToAdd) ?: println("Could not add $srcDirToAdd to $sourceSetName source set.")
    }
}
