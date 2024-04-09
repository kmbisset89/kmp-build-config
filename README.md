# KMP BuildConfig Plugin

[![License](https://img.shields.io/github/license/cortinico/kotlin-android-template.svg)](LICENSE) ![Language](https://img.shields.io/github/languages/top/cortinico/kotlin-android-template?color=blue&logo=kotlin)

The KMP BuildConfig Plugin is a Gradle plugin designed to generate a `BuildConfig.kt` file for Kotlin Multiplatform projects. This plugin fills the gap for non-Android modules where a `BuildConfig` class is not automatically generated, allowing you to include project-specific constants in your Kotlin Multiplatform project in a similar manner.

## How to use 👣

### Installation

To use the plugin, add the following to your project's `build.gradle.kts` file:

```kotlin
plugins {
    id("io.github.kmbisset89.kmpbuildconfig.plugin") version "1.0.0"
}
```

For libs.toml:

```toml
[versions]
  build-config = "1.0.0"

[plugins]
build-config-generator = { id = "io.github.kmbisset89.kmpbuildconfig.plugin", version.ref = "build-config" }
```

### Configuration

After applying the plugin, configure it by setting the necessary properties in the plugin's extension block:

The options for configProperties are :
- withString : A non-null string value
- withOptionalString : A nullable string value
- withInt : An integer value
- withOptionalInt : An optional integer value
- withLong : A long value
- withOptionalLong : An optional long value
- withFloat : A float value
- withOptionalFloat : An optional float value
- withDouble : A double value
- withOptionalDouble : An optional double value
- withBool : A boolean value
- withOptionalBool : An optional boolean value

```kotlin
kmpBuildConfig {
    packageName.set("your.app.package")
    this.buildConfigFileName.set("BuildConfig")
    configProperties {
        "version" withString rootProject.version.toString()
        "test" withObj {
            "test2" withString "test2"
        }
    }
}
```

Output:

```kotlin
public object BuildConfig {
    public val VERSION: String = "0.1.1-rc.1"

    public object Test {
        public val TEST2: String = "test2"
    }
}
```



#### Set to createBuildConfig task to run before build

```kotlin
tasks.build.dependsOn(tasks.createBuildConfig)
```

#### Add sources to the sourceSets

```kotlin
val addToSources = task("addGeneratedToSources") {
    kotlin.sourceSets["commonMain"].kotlin.srcDir("build/generated/source/buildConfig")
    tasks.createBuildConfig{
        finalizedBy(this@task)
    }
}
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
