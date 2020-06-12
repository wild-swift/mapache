package name.wildswift.mapache.generator.codegen

import com.squareup.javapoet.*
import name.wildswift.mapache.events.Event
import name.wildswift.mapache.generator.generatemodel.Action
import name.wildswift.mapache.generator.toType
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Modifier


class ActionsGenerator(
        private val prefix: String,
        private val packageName: String,
        private val processingEnv: ProcessingEnvironment,
        private val actions: List<Action>
) {

    val baseTypeName = ClassName.get(packageName, "${prefix}Event")
    val actionNames = actions.map { action -> action to ClassName.get(packageName, action.name) }.toMap()


    fun generateAll() {

        val baseInterfaceTypeSpec = TypeSpec
                .interfaceBuilder(baseTypeName)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ClassName.get(Event::class.java))
                .build()

        processingEnv.filer.createSourceFile(baseTypeName.canonicalName())
                .openWriter()
                .use { fileWriter ->
                    JavaFile.builder(packageName, baseInterfaceTypeSpec)
                            .build()
                            .writeTo(fileWriter)
                }

        actions.forEach { action ->
            val actionName = actionNames[action] ?: error("Internal error")

            val actionTypeSpecBuilder = TypeSpec
                    .classBuilder(actionName)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addSuperinterface(baseTypeName)

            if (action.params.isEmpty()) {
                val instanceField = FieldSpec.builder(actionName, "instance", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL).initializer("new \$T()", actionName).build()
                actionTypeSpecBuilder
                        .addField(instanceField)
                        .addMethod(MethodSpec.methodBuilder("newInstance").addModifiers(Modifier.PUBLIC, Modifier.STATIC).returns(actionName).addStatement("return \$N", instanceField).build())
                        .addMethod(MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build())
            } else {
                val paramenters = action.params.map { it.name to it.type.toType() }
                actionTypeSpecBuilder
                        .addMethod(MethodSpec.methodBuilder("newInstance")
                                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                                .addParameters(paramenters.map { (name, type) -> ParameterSpec.builder(type, name).build() })
                                .returns(actionName)
                                .addStatement("return new \$T(${paramenters.joinToString(", ") { (name, _) -> name }})", actionName)
                                .build())
                        .addFields(paramenters.map { (name, type) -> FieldSpec.builder(type, name, Modifier.PRIVATE, Modifier.FINAL).build() })
                        .addMethod(MethodSpec.constructorBuilder()
                                .addModifiers(Modifier.PRIVATE)
                                .addParameters(paramenters.map { (name, type) -> ParameterSpec.builder(type, name).build() })
                                .apply {
                                    paramenters.forEach { (name, _) ->
                                        addStatement("this.$name = $name")
                                    }
                                }
                                .build())
                        .addMethods(paramenters.map { (name, type) ->
                            MethodSpec.methodBuilder("get${name.capitalize()}")
                                    .addModifiers(Modifier.PUBLIC)
                                    .returns(type)
                                    .addStatement("return $name")
                                    .build()
                        })
                        .addMethods(paramenters.mapIndexed { i, (name, type) ->
                            MethodSpec.methodBuilder("component${i+1}")
                                    .addModifiers(Modifier.PUBLIC)
                                    .returns(type)
                                    .addStatement("return $name")
                                    .build()
                        })
                        .addMethod(MethodSpec.methodBuilder("equals")
                                .addModifiers(Modifier.PUBLIC)
                                .addAnnotation(Override::class.java)
                                .addAnnotation(AnnotationSpec.builder(SuppressWarnings::class.java).addMember("value", "\"EqualsReplaceableByObjectsCall\"").build())
                                .returns(TypeName.BOOLEAN)
                                .addParameter(ParameterSpec.builder(TypeName.OBJECT, "o").build())
                                .addStatement("if (this == o) return true")
                                .addStatement("if (o == null || getClass() != o.getClass()) return false")
                                .addStatement("\$1T that = (\$1T) o", actionName)
                                .apply {
                                    paramenters.forEach { (name, type) ->
                                        if (type.isPrimitive) {
                                            addStatement("if (${name} != that.${name}) return false")
                                        } else {
                                            addStatement("if (${name} != null ? !${name}.equals(that.${name}) : that.${name} != null) return false")
                                        }
                                    }
                                }
                                .addStatement("return true")
                                .build())
                        .addMethod(MethodSpec.methodBuilder("hashCode")
                                .addModifiers(Modifier.PUBLIC)
                                .addAnnotation(Override::class.java)
                                .addAnnotation(AnnotationSpec.builder(SuppressWarnings::class.java).addMember("value", "\"ConstantConditions\"").build())
                                .returns(TypeName.INT)
                                .addStatement("int result = 0")
                                .apply {
                                    paramenters.forEach { (name, type) ->
//                                        result = 31 * result + (pFloat != +0.0f ? Float.floatToIntBits(pFloat) : 0);
//                                        result = 31 * result + (int) (pLong ^ (pLong >>> 32));
//                                        long pDoubleBits = Double.doubleToLongBits(pDouble);
//                                        result = 31 * result + (int) (pDoubleBits ^ (pDoubleBits >>> 32));
                                        if (type == TypeName.BOOLEAN) {
                                            addStatement("result = 31 * result + ($name ? 1 : 0)")
                                        } else if (type == TypeName.BYTE || type == TypeName.SHORT || type == TypeName.CHAR) {
                                            addStatement("result = 31 * result + (int) $name")
                                        } else if (type == TypeName.INT) {
                                            addStatement("result = 31 * result + $name")
                                        } else if (type == TypeName.FLOAT) {
                                            addStatement("result = 31 * result + ($name != +0.0f ? Float.floatToIntBits($name) : 0)")
                                        } else if (type == TypeName.LONG) {
                                            addStatement("result = 31 * result + (int) ($name ^ ($name >>> 32))")
                                        } else if (type == TypeName.DOUBLE) {
                                            addStatement("long ${name}Bits = Double.doubleToLongBits(${name});")
                                            addStatement(" result = 31 * result + (int) (${name}Bits ^ (${name}Bits >>> 32))")
                                        } else {
                                            addStatement("result = 31 * result + ($name != null ? $name.hashCode() : 0)")
                                        }
                                    }
                                }
                                .addStatement("return result")
                                .build())
            }

            processingEnv.filer.createSourceFile(actionName.canonicalName())
                    .openWriter()
                    .use { fileWriter ->
                        JavaFile.builder(packageName, actionTypeSpecBuilder.build())
                                .build()
                                .writeTo(fileWriter)
                    }

        }
    }
}