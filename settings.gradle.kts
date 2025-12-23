pluginManagement {
    // Make the plugin available to the `plugins { ... }` DSL from the local sources
    // so the `example` module can use it without publishing.
    includeBuild("plugin-build")
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
    }
}

plugins {
    `gradle-enterprise`
}

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
        publishAlwaysIf(System.getenv("GITHUB_ACTIONS") == "true")
        publishOnFailure()
    }
}

rootProject.name = "KMP-Build-Config"

include(":example")
includeBuild("plugin-build")
