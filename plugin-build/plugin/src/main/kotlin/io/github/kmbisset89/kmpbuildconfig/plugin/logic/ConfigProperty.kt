package io.github.kmbisset89.kmpbuildconfig.plugin.logic

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional
import kotlin.reflect.KType

sealed class ConfigProperty {


    open class ObjectConfigProperty(
        @Input
        val name: String,
        @Nested
        val properties: List<ConfigProperty>
    ) : ConfigProperty() {
        fun build(typeSpecBuilder: TypeSpec.Builder, fileSpecBuilder: FileSpec.Builder) {
            val type = TypeSpec.objectBuilder(name).also { b ->
                properties.forEach {
                    when (it) {
                        is PrimitiveConfigProperty<*> -> it.build(b)
                        is ObjectConfigProperty -> it.build(b, fileSpecBuilder)
                    }
                }
            }.build()
            typeSpecBuilder.addType(type)
        }
    }

    open class PrimitiveConfigProperty<T>(
        @Input
        val name: String,
        @Internal
        val type: KType,
        @Input
        val template: String,
        @Input
        @Optional
        val value: T
    ) : ConfigProperty() {
        fun build(typeSpecBuilder: TypeSpec.Builder) {
            val prop = PropertySpec
                .builder(name, type.asTypeName())
                .initializer(template, value)
                .build()
            typeSpecBuilder.addProperty(prop)
        }
    }
}
