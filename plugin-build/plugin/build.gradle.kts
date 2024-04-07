import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.FileInputStream
import java.util.*

plugins {
    kotlin("jvm")
    `java-gradle-plugin`
//    alias(libs.plugins.kotlin.gradle.plugn)
    alias(libs.plugins.pluginPublish)
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(gradleApi())
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.kotlin.poet)
    testImplementation(libs.junit5.api)
    testImplementation(libs.junit5.engine)
    testImplementation(libs.junit5.params)
    testImplementation(libs.junit5.platform)
    testImplementation(libs.mockk)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

gradlePlugin {
    plugins {
        create(property("ID").toString()) {
            id = property("ID").toString()
            implementationClass = property("IMPLEMENTATION_CLASS").toString()
            version = property("VERSION").toString()
            description = property("DESCRIPTION").toString()
            displayName = property("DISPLAY_NAME").toString()
            tags.set(listOf("SemVer", "Semantic Versioning", "Versioning", "Git", "Tags", "Version", "Versioning Plugin", "Versioning Plugin for Git Tags"))
        }
    }
}

gradlePlugin {
    website.set(property("WEBSITE").toString())
    vcsUrl.set(property("VCS_URL").toString())
}

java{
    withSourcesJar()
}

tasks.create("setupPluginUploadFromEnvironment") {
    doLast {
        val localProperties = if(rootProject.file("local.properties").exists()) Properties() else null
        localProperties?.load(FileInputStream(rootProject.file("local.properties")))

        val key = localProperties?.get("gradle.publish.key") as? String ?: System.getenv("GRADLE_PUBLISH_KEY")
        val secret = localProperties?.get("gradle.publish.secret") as? String ?: System.getenv("GRADLE_PUBLISH_SECRET")

        if (key == null || secret == null) {
            throw GradleException("gradlePublishKey and/or gradlePublishSecret are not defined environment variables")
        }

        System.setProperty("gradle.publish.key", key)
        System.setProperty("gradle.publish.secret", secret)
    }
}

tasks.test {
    useJUnitPlatform()
}
