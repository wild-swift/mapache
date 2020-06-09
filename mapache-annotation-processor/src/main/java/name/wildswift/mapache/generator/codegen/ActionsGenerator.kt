package name.wildswift.mapache.generator.codegen

import com.squareup.javapoet.*
import name.wildswift.mapache.generator.find
import name.wildswift.mapache.generator.generatemodel.Action
import name.wildswift.mapache.generator.resolveType
import name.wildswift.mapache.generator.singletone
import org.w3c.dom.Node
import java.lang.IllegalStateException
import java.util.*
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Modifier


class ActionsGenerator(
        private val prefix: String,
        private val packageName: String,
        private val processingEnv: ProcessingEnvironment
) {

    fun generateAll(actions: List<Action>) {

        val baseInterfaceName = ClassName.get(packageName, "${prefix}Event")

        val baseInterfaceTypeSpec = TypeSpec
                .interfaceBuilder(baseInterfaceName)
                .addSuperinterface(ClassName.get("name.wildswift.mapache.events", "Event"))
                .build()

        processingEnv.filer.createSourceFile(baseInterfaceName.canonicalName())
                .openWriter()
                .use { fileWriter ->
                    JavaFile.builder(packageName, baseInterfaceTypeSpec)
                            .build()
                            .writeTo(fileWriter)
                }

        actions.forEach { action ->
            val actionName = ClassName.get(packageName, action.name)
            val actionTypeSpecBuilder = TypeSpec
                    .classBuilder(actionName)
                    .addSuperinterface(baseInterfaceName)

            if (action.params.isEmpty()) {
                val instanceField = FieldSpec.builder(actionName, "instance", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL).initializer("new \$T()", actionName).build()
                actionTypeSpecBuilder
                        .addField(instanceField)
                        .addMethod(MethodSpec.methodBuilder("newInstance").addModifiers(Modifier.PUBLIC, Modifier.STATIC).returns(actionName).addStatement("return \$N", instanceField).build())
                        .addMethod(MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build())
            } else {
                val paramenters = action.params.map { it.name to ClassName.get(it.type.split(".").dropLast(1).joinToString("."), it.type.split(".").lastOrNull()) }
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
                                .returns(TypeName.BOOLEAN)
                                .addParameter(ParameterSpec.builder(TypeName.OBJECT, "o").build())
                                .addStatement("if (this == o) return true")
                                .addStatement("if (o == null || getClass() != o.getClass()) return false")
                                .addStatement("\$1T that = (\$1T) o", actionName)

                                .addStatement("return true")
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

//        val children = rootNode.childNodes.find(prefix = nsPrefix, name = "action")
//        val backName = rootNode.attributes.find(prefix = nsPrefix, name = "backAction").singleOrNull()?.nodeValue
//                ?: throw IllegalArgumentException("Mapache.xml file not valid")
//
//        children.forEach {
//            generateActionClass(it)
//        }
//        generateBackClass(backName)
//
//        val factoryClassName = ClassName.get(packageName, "${prefix}SystemEventFactory")
//        val eventsRootClassName = baseInterfaceName
//        val backEventMethod = generateEventBuilderMethod(backName, listOf())
//
//        val eventsClass = TypeSpec.classBuilder(eventsRootClassName)
//                .addModifiers(Modifier.PUBLIC)
//                .addSuperinterface(ClassName.get("name.wildswift.mapache.events", "Event"))
//                .apply {
//                    children.forEach {
//                        val eventName = it.attributes.find(prefix = nsPrefix, name = "name").singleOrNull()?.nodeValue
//                                ?: throw IllegalArgumentException("Mapache.xml file not valid")
//
//                        addMethod(generateEventBuilderMethod(eventName, getArgs(it)))
//                    }
//                    addMethod(backEventMethod)
//                }
//                .build()
//
//        val eventsJavaFile = JavaFile.builder(packageName, eventsClass)
//                .build()
//
//        if (DEBUG) {
//            eventsJavaFile.writeTo(System.out)
//        }
//        eventsJavaFile.writeTo(output)
//
//        val factoryClass = TypeSpec.classBuilder(factoryClassName)
//                .addModifiers(Modifier.PUBLIC)
//                .addSuperinterface(ParameterizedTypeName
//                        .get(
//                                ClassName.get("name.wildswift.mapache.events", "SystemEventFactory"),
//                                eventsRootClassName
//                        )
//                )
//                .singletone(factoryClassName)
//                .addMethod(MethodSpec.methodBuilder("getBackEvent")
//                        .returns(eventsRootClassName)
//                        .addModifiers(Modifier.PUBLIC)
//                        .addAnnotation(Override::class.java)
//                        .addStatement("return \$T.\$N()", eventsRootClassName, backEventMethod)
//                        .build())
//                .build()
//
//        val factoryJavaFile = JavaFile.builder(packageName, factoryClass)
//                .build()
//
//        if (DEBUG) {
//            factoryJavaFile.writeTo(System.out)
//        }
//        factoryJavaFile.writeTo(output)
    }

    private fun generateActionClass(actionNode: Node) {
//        val eventName = actionNode.attributes.find(prefix = nsPrefix, name = "name").singleOrNull()?.nodeValue ?: throw IllegalArgumentException("Mapache.xml file not valid")
//
//        val constructor = MethodSpec.constructorBuilder()
//
//        val singleEventClass = TypeSpec.classBuilder(eventName)
//                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
//                .superclass(ClassName.get(packageName, "${prefix}Event"))
//                .apply {
//                    getArgs(actionNode).forEach { (name, type) ->
//                        val newField = FieldSpec.builder(resolveType(type), name, Modifier.PRIVATE, Modifier.FINAL).build()
//                        val newParameter = ParameterSpec.builder(resolveType(type), name).build()
//
//                        addField(newField)
//
//                        constructor.addParameter(newParameter)
//                        constructor.addStatement("this.\$N = \$N", newField, newParameter)
//                    }
//                }
//                .addMethod(constructor.build())
//                .build()
//
//        val javaFile = JavaFile.builder(packageName, singleEventClass).build()
//
//        if (DEBUG) {
//            javaFile.writeTo(System.out)
//        }
//        javaFile.writeTo(output)
    }

    private fun generateBackClass(name: String) {
//        val singleEventClass = TypeSpec.classBuilder(name)
//                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
//                .superclass(ClassName.get(packageName, "${prefix}Event"))
//                .addMethod(MethodSpec.constructorBuilder().build())
//                .build()
//
//        val javaFile = JavaFile.builder(packageName, singleEventClass).build()
//
//        if (DEBUG) {
//            javaFile.writeTo(System.out)
//        }
//        javaFile.writeTo(output)
    }

//    private fun generateEventBuilderMethod(eventName: String, arguments: List<Pair<String, String>>): MethodSpec {
//        val eventClass = ClassName.get(packageName, eventName)
//        return MethodSpec.methodBuilder(eventName.decapitalize())
//                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
//                .returns(eventClass)
//                .apply {
//                    arguments.forEach { (name, type) ->
//                        addParameter(resolveType(type), name)
//                    }
//                    addStatement("return new \$T(${arguments.joinToString { it.first }})", eventClass)
//                }
//                .build()
//    }

//    private fun getArgs(actionNode: Node): List<Pair<String, String>> {
//        return actionNode.childNodes.find(prefix = nsPrefix, name = "arg").map {
//            val type = it.attributes.find(prefix = nsPrefix, name = "type").singleOrNull()?.nodeValue
//                    ?: throw IllegalArgumentException("Mapache.xml file not valid")
//            val name = it.attributes.find(prefix = nsPrefix, name = "name").singleOrNull()?.nodeValue
//                    ?: throw IllegalArgumentException("Mapache.xml file not valid")
//
//            name to type
//        }
//    }

    companion object {
        private const val DEBUG = false
    }
}