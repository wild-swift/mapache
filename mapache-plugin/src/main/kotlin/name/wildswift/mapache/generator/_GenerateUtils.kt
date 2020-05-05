package name.wildswift.mapache.generator

import com.squareup.javapoet.*
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