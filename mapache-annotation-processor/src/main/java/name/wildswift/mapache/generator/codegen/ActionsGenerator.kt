package name.wildswift.mapache.generator.codegen

import com.squareup.javapoet.*
import name.wildswift.mapache.events.Event
import name.wildswift.mapache.generator.addDataClassFields
import name.wildswift.mapache.generator.generatemodel.EventDefinition
import name.wildswift.mapache.generator.generatemodel.GenerateModel
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Modifier


class ActionsGenerator(
        private val packageName: String,
        val baseEventClass: ClassName,
        val events: List<EventDefinition>,
        private val processingEnv: ProcessingEnvironment
) {
    fun generateAll() {

        val baseInterfaceTypeSpec = TypeSpec
                .interfaceBuilder(baseEventClass)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ClassName.get(Event::class.java))
                .build()

        processingEnv.filer.createSourceFile(baseEventClass.canonicalName())
                .openWriter()
                .use { fileWriter ->
                    JavaFile.builder(packageName, baseInterfaceTypeSpec)
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

            processingEnv.filer.createSourceFile(action.typeName.canonicalName())
                    .openWriter()
                    .use { fileWriter ->
                        JavaFile.builder(packageName, actionTypeSpecBuilder.build())
                                .build()
                                .writeTo(fileWriter)
                    }

        }
    }
}