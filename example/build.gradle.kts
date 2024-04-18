plugins {
    kotlin("multiplatform")
    id("io.github.kmbisset89.kmpbuildconfig.plugin")
}


kotlin{
    jvm("desktop")


    sourceSets{
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib")
            }
        }

        val desktopMain by getting{

        }
    }
}

kmpBuildConfig {
    packageName.set("io.github.kmbisset89.kmpbuildconfig")
    buildConfigFileName.set("BuildConfig")
    secretKeyFileName.set("Secret")
    sourceDir.set(kotlin.sourceSets.findByName("commonMain")!!.kotlin)
    configProperties {
        "testProperty" to "testValue"
    }
}


