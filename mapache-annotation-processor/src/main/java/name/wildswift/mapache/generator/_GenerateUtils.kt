package name.wildswift.mapache.generator

import com.squareup.javapoet.*
import name.wildswift.mapache.generator.generatemodel.ParameterDefinition
import javax.lang.model.element.Modifier

fun resolveType(typeToString: String): TypeName = when {
    typeToString.indexOf(".") >= 0 -> {
        ClassName.get(typeToString.split('.').dropLast(1).joinToString("."), typeToString.split('.').last())
    }
    typeToString == "string" -> ClassName.get(String::class.java)
    typeToString == "int" -> TypeName.INT
    typeToString == "bool" -> TypeName.BOOLEAN
    typeToString == "char" -> TypeName.CHAR
    typeToString == "byte" -> TypeName.BYTE
    typeToString == "long" -> TypeName.LONG
    typeToString == "float" -> TypeName.FLOAT
    typeToString == "double" -> TypeName.DOUBLE
    typeToString == "short" -> TypeName.SHORT
    else -> throw IllegalArgumentException("Mapache.xml file not valid")
}

fun TypeSpec.Builder.singletone(factoryClassName: ClassName): TypeSpec.Builder {
    val instanceField = FieldSpec.builder(factoryClassName, "instance", Modifier.PRIVATE, Modifier.STATIC).build()
    addMethod(MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build())
    addField(instanceField)
    addMethod(MethodSpec.methodBuilder("instance")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(factoryClassName)
            .addCode(CodeBlock.of("""
                | if (${'$'}N == null) {
                |   ${'$'}N = new ${'$'}T();
                | }
                | return ${'$'}N;
                | 
            """.trimMargin(), instanceField, instanceField, factoryClassName, instanceField))
            .build()
    )

    return this
}

@SuppressWarnings("DefaultLocale")
fun TypeSpec.Builder.addDataClassFields(params: List<ParameterDefinition>, typeName: ClassName): TypeSpec.Builder {
    addFields(params.map { (name, type) -> FieldSpec.builder(type, name, Modifier.PRIVATE, Modifier.FINAL).build() })
    addMethod(MethodSpec.constructorBuilder()
            .addModifiers(Modifier.PRIVATE)
            .addParameters(params.map { (name, type) -> ParameterSpec.builder(type, name).build() })
            .apply {
                params.forEach { (name, _) ->
                    addStatement("this.$name = $name")
                }
            }
            .build())
    addMethods(params.map { (name, type) ->
        MethodSpec.methodBuilder("get${name.capitalize()}")
                .addModifiers(Modifier.PUBLIC)
                .returns(type)
                .addStatement("return $name")
                .build()
    })
    addMethods(params.map { (name, type) ->
        MethodSpec.methodBuilder(name)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(type, "value").build())
                .returns(typeName)
                .addStatement("return new \$T(${params.joinToString { if (it.name == name) "value" else "this.${it.name}" }})", typeName)
                .build()
    })
    addMethods(params.mapIndexed { i, (name, type) ->
        MethodSpec.methodBuilder("component${i + 1}")
                .addModifiers(Modifier.PUBLIC)
                .returns(type)
                .addStatement("return $name")
                .build()
    })
    addMethod(MethodSpec.methodBuilder("equals")
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Override::class.java)
            .addAnnotation(AnnotationSpec.builder(SuppressWarnings::class.java).addMember("value", "\"EqualsReplaceableByObjectsCall\"").build())
            .returns(TypeName.BOOLEAN)
            .addParameter(ParameterSpec.builder(TypeName.OBJECT, "o").build())
            .addStatement("if (this == o) return true")
            .addStatement("if (o == null || getClass() != o.getClass()) return false")
            .addStatement("\$1T that = (\$1T) o", typeName)
            .apply {
                params.forEach { (name, type) ->
                    if (type.isPrimitive) {
                        addStatement("if (${name} != that.${name}) return false")
                    } else {
                        addStatement("if (${name} != null ? !${name}.equals(that.${name}) : that.${name} != null) return false")
                    }
                }
            }
            .addStatement("return true")
            .build())
    addMethod(MethodSpec.methodBuilder("hashCode")
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Override::class.java)
            .addAnnotation(AnnotationSpec.builder(SuppressWarnings::class.java).addMember("value", "\"ConstantConditions\"").build())
            .returns(TypeName.INT)
            .addStatement("int result = 0")
            .apply {
                params.forEach { (name, type) ->
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
    return this
}