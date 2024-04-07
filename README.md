# KMP BuildConfig Plugin

[![License](https://img.shields.io/github/license/cortinico/kotlin-android-template.svg)](LICENSE) ![Language](https://img.shields.io/github/languages/top/cortinico/kotlin-android-template?color=blue&logo=kotlin)

The KMP BuildConfig Plugin is a Gradle plugin designed to generate a `BuildConfig.kt` file for Kotlin Multiplatform projects. This plugin fills the gap for non-Android modules where a `BuildConfig` class is not automatically generated, allowing you to include project-specific constants in your Kotlin Multiplatform project in a similar manner.

## How to use 👣

### Installation

To use the plugin, add the following to your project's `build.gradle.kts` file:

```kotlin
plugins {
    id("io.github.kmbisset89.kmpbuildconfig.plugin") version "0.1.0"
}
```

For libs.toml:

```toml
[versions]
  build-config = "0.1.0"

[plugins]
build-config-generator = { id = "io.github.kmbisset89.kmpbuildconfig.plugin", version.ref = "build-config" }
```

### Configuration

After applying the plugin, configure it by setting the necessary properties in the plugin's extension block:

```kotlin
semVerConfig {
    kmpBuildConfig {
        versionNumber = "1.0.0" // Your project's version
        packageName = "com.example.project" // The package name for the BuildConfig file
        sourceSetName = "commonMain" // Optional: The source set name (defaults to commonMain)
        buildConfigFileName = "BuildConfig.kt" // Optional: The BuildConfig file name (defaults to BuildConfig.kt)
        buildConfigProperties = mapOf(
            "API_URL" to "https://api.example.com",
            "DEBUG" to "true"
        ) // Optional: Additional properties to include in the BuildConfig
    }
}
```

#### Set to createBuildConfig task to run before build

```kotlin
tasks.getByName("build").finalizedBy("createBuildConfig")
```


### Generating the BuildConfig

Run the createBuildConfig Gradle task to generate the BuildConfig.kt file:


```bash
./gradlew createBuildConfig
```


## Features 🎨

- Generates a `BuildConfig.kt` file with customizable properties.
- Supports specifying the package name, version number, and additional properties to include in the `BuildConfig`.
- Allows setting the source set name and the build config file's name through the plugin's extension.
- Easy to integrate into existing Kotlin Multiplatform projects.

## Contributing 🤝

Feel free to open an issue or submit a pull request for any bugs/improvements.

## License 📄

This template is licensed under the MIT License - see the [License](License) file for details.
Please note that the generated template is offering to start with a MIT license but you can change it to whatever you
wish, as long as you attribute under the MIT terms that you're using the template.
