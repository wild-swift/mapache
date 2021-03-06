package name.wildswift.mapache.generator.codegen

import com.squareup.javapoet.*
import name.wildswift.mapache.events.Event
import name.wildswift.mapache.generator.addDataClassFields
import name.wildswift.mapache.generator.generatemodel.EventDefinition
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier


class ActionsGenerator(
        private val baseEventClass: ClassName,
        private val events: List<EventDefinition>,
        private val filer: Filer
) {
    fun generateAll() {
        val baseInterfaceTypeSpec = TypeSpec
                .interfaceBuilder(baseEventClass)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ClassName.get(Event::class.java))
                .build()

        filer.createSourceFile(baseEventClass.canonicalName())
                .openWriter()
                .use { fileWriter ->
                    JavaFile.builder(baseEventClass.packageName(), baseInterfaceTypeSpec)
                            .build()
                            .writeTo(fileWriter)
                }

        events.forEach { action ->
            val actionTypeSpecBuilder = TypeSpec
                    .classBuilder(action.typeName)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addSuperinterface(baseEventClass)

            if (action.params.isEmpty()) {
                val instanceField = FieldSpec.builder(action.typeName, "instance", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL).initializer("new \$T()", action.typeName).build()
                actionTypeSpecBuilder
                        .addField(instanceField)
                        .addMethod(MethodSpec.methodBuilder("newInstance").addModifiers(Modifier.PUBLIC, Modifier.STATIC).returns(action.typeName).addStatement("return \$N", instanceField).build())
                        .addMethod(MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build())
            } else {
                actionTypeSpecBuilder
                        .addMethod(MethodSpec.methodBuilder("newInstance")
                                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                                .addParameters(action.params.map { (name, type) -> ParameterSpec.builder(type, name).build() })
                                .returns(action.typeName)
                                .addStatement("return new \$T(${action.params.joinToString(", ") { (name, _) -> name }})", action.typeName)
                                .build())
                        .addDataClassFields(action.params, action.typeName)
            }

            filer.createSourceFile(action.typeName.canonicalName())
                    .openWriter()
                    .use { fileWriter ->
                        JavaFile.builder(action.typeName.packageName(), actionTypeSpecBuilder.build())
                                .build()
                                .writeTo(fileWriter)
                    }

        }
    }
}