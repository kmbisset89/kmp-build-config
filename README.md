# KMP BuildConfig Plugin

[![License](https://img.shields.io/github/license/cortinico/kotlin-android-template.svg)](LICENSE) ![Language](https://img.shields.io/github/languages/top/cortinico/kotlin-android-template?color=blue&logo=kotlin)

The KMP BuildConfig Plugin is a Gradle plugin designed to generate a `BuildConfig.kt` file for Kotlin Multiplatform projects. This plugin fills the gap for non-Android modules where a `BuildConfig` class is not automatically generated, allowing you to include project-specific constants in your Kotlin Multiplatform project in a similar manner.

## How to use 👣

### Installation

To use the plugin, add the following to your project's `build.gradle.kts` file:

```kotlin
plugins {
    id("io.github.kmbisset89.kmpbuildconfig.plugin") version "1.0.9"
}
```

For libs.toml:

```toml
[versions]
  build-config = "1.0.9"

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
    val localProps = Properties().also {
        it.load(file(rootProject.file("local.properties").path).inputStream())
    }
    packageName.set("com.example.yourproject")
    buildConfigFileName.set("BuildConfig")
    secretKeyFileName.set("SecretKey")
    configProperties {
        sourceSet("commonMain") {
            "version" withString rootProject.version.toString()
        }

        sourceSet("androidMain") {
            // Convenient alias for strings:
            "apiBaseUrl" to "https://example.com"

            "connectionString" withSecretString (localProps.getProperty("connectionString") guardedBy UUID.randomUUID()
                .toString())
        }
    }
}
```

#### Notes about multiple source sets
- If you configure **more than one** `sourceSet("...")`, the plugin will generate **distinct objects** to avoid duplicate classes on platform compilations.
  - Example: with `buildConfigFileName.set("BuildConfig")`, you’ll get `BuildConfigCommonMain` and `BuildConfigAndroidMain`.

Output:

```kotlin
public object BuildConfig {
    public val VERSION: String = "0.1.1-rc.2"

    public val CONNECTION_STRING: String =
        "TopkevdOxnzysxdcZbydymyv=rddzc;KmmyexdXkwo=yzpvnkdklkco;KmmyexdUoi=kSW+a76Ojj5nVJKEEdwrKAP7FCrI/C6NSaQBpBKpRWHzwi1z2jiE1L3mXAD9xNrXdtJ21Jzxd8Ze+KCddNSXUA==;OxnzysxdCeppsh=mybo.ecqyfmvyenkzs.xod"
}


public object SecretKey {
    public val CONNECTION_STRING_KEY: String = "da193df1-dd57-4b02-93d5-245d72ebdsads"
}

//Use in Code
val connectionString = CryptoUtils.decrypt(BuildConfig.CONNECTION_STRING, SecretKey.CONNECTION_STRING_KEY)

```

### Notes About Secrets:
This is about as secure as sending an email with the username and then one with the password. You are hoping that someone does not hack your email account but if they did they would technically have access to both. This is a way to keep the secrets in plain text out of the codebase but it is not a foolproof way to keep them secure. If you need to keep them secure, you should look into a secret management system. This is a little harder to consider for say and offline android application, where you need to store the secret in the app or the file system, but trying to distribute new files would be hard.


#### Set to createBuildConfig task to run before build

```kotlin
tasks.build.dependsOn(tasks.createBuildConfig)
```

#### Add sources to the sourceSets
#### This is now no longer necessary in 1.0.9+

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
- Supports configuring properties per Kotlin source set via `configProperties { sourceSet("...") { ... } }`.
- Easy to integrate into existing Kotlin Multiplatform projects.

## Contributing 🤝

Feel free to open an issue or submit a pull request for any bugs/improvements.

## License 📄

This template is licensed under the MIT License - see the [License](License) file for details.
Please note that the generated template is offering to start with a MIT license but you can change it to whatever you
wish, as long as you attribute under the MIT terms that you're using the template.
