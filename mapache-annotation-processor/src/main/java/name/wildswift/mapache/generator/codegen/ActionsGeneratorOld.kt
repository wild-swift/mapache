package name.wildswift.mapache.generator.codegen

import com.squareup.javapoet.*
import name.wildswift.mapache.generator.find
import name.wildswift.mapache.generator.resolveType
import name.wildswift.mapache.generator.singletone
import org.w3c.dom.Node
import java.io.File
import javax.lang.model.element.Modifier


class ActionsGeneratorOld(
        private val nsPrefix: String?,
        private val prefix: String,
        private val packageName: String,
        private val output: File
) {

    fun generateAll(rootNode: Node) {
        val children = rootNode.childNodes.find(prefix = nsPrefix, name = "action")
        val backName = rootNode.attributes.find(prefix = nsPrefix, name = "backAction").singleOrNull()?.nodeValue
                ?: throw IllegalArgumentException("Mapache.xml file not valid")

        children.forEach {
            generateActionClass(it)
        }
        generateBackClass(backName)

        val factoryClassName = ClassName.get(packageName, "${prefix}SystemEventFactory")
        val eventsRootClassName = ClassName.get(packageName, "${prefix}Event")
        val backEventMethod = generateEventBuilderMethod(backName, listOf())

        val eventsClass = TypeSpec.classBuilder(eventsRootClassName)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ClassName.get("name.wildswift.mapache.events", "Event"))
                .apply {
                    children.forEach {
                        val eventName = it.attributes.find(prefix = nsPrefix, name = "name").singleOrNull()?.nodeValue
                                ?: throw IllegalArgumentException("Mapache.xml file not valid")

                        addMethod(generateEventBuilderMethod(eventName, getArgs(it)))
                    }
                    addMethod(backEventMethod)
                }
                .build()

        val eventsJavaFile = JavaFile.builder(packageName, eventsClass)
                .build()

        if (DEBUG) {
            eventsJavaFile.writeTo(System.out)
        }
        eventsJavaFile.writeTo(output)

        val factoryClass = TypeSpec.classBuilder(factoryClassName)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ParameterizedTypeName
                        .get(
                                ClassName.get("name.wildswift.mapache.events", "SystemEventFactory"),
                                eventsRootClassName
                        )
                )
                .singletone(factoryClassName)
                .addMethod(MethodSpec.methodBuilder("getBackEvent")
                        .returns(eventsRootClassName)
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(Override::class.java)
                        .addStatement("return \$T.\$N()", eventsRootClassName, backEventMethod)
                        .build())
                .build()

        val factoryJavaFile = JavaFile.builder(packageName, factoryClass)
                .build()

        if (DEBUG) {
            factoryJavaFile.writeTo(System.out)
        }
        factoryJavaFile.writeTo(output)
    }

    private fun generateActionClass(actionNode: Node) {
        val eventName = actionNode.attributes.find(prefix = nsPrefix, name = "name").singleOrNull()?.nodeValue ?: throw IllegalArgumentException("Mapache.xml file not valid")

        val constructor = MethodSpec.constructorBuilder()

        val singleEventClass = TypeSpec.classBuilder(eventName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .superclass(ClassName.get(packageName, "${prefix}Event"))
                .apply {
                    getArgs(actionNode).forEach { (name, type) ->
                        val newField = FieldSpec.builder(resolveType(type), name, Modifier.PRIVATE, Modifier.FINAL).build()
                        val newParameter = ParameterSpec.builder(resolveType(type), name).build()

                        addField(newField)

                        constructor.addParameter(newParameter)
                        constructor.addStatement("this.\$N = \$N", newField, newParameter)
                    }
                }
                .addMethod(constructor.build())
                .build()

        val javaFile = JavaFile.builder(packageName, singleEventClass).build()

        if (DEBUG) {
            javaFile.writeTo(System.out)
        }
        javaFile.writeTo(output)
    }

    private fun generateBackClass(name: String) {
        val singleEventClass = TypeSpec.classBuilder(name)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .superclass(ClassName.get(packageName, "${prefix}Event"))
                .addMethod(MethodSpec.constructorBuilder().build())
                .build()

        val javaFile = JavaFile.builder(packageName, singleEventClass).build()

        if (DEBUG) {
            javaFile.writeTo(System.out)
        }
        javaFile.writeTo(output)
    }

    private fun generateEventBuilderMethod(eventName: String, arguments: List<Pair<String, String>>): MethodSpec {
        val eventClass = ClassName.get(packageName, eventName)
        return MethodSpec.methodBuilder(eventName.decapitalize())
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(eventClass)
                .apply {
                    arguments.forEach { (name, type) ->
                        addParameter(resolveType(type), name)
                    }
                    addStatement("return new \$T(${arguments.joinToString { it.first }})", eventClass)
                }
                .build()
    }

    private fun getArgs(actionNode: Node): List<Pair<String, String>> {
        return actionNode.childNodes.find(prefix = nsPrefix, name = "arg").map {
            val type = it.attributes.find(prefix = nsPrefix, name = "type").singleOrNull()?.nodeValue
                    ?: throw IllegalArgumentException("Mapache.xml file not valid")
            val name = it.attributes.find(prefix = nsPrefix, name = "name").singleOrNull()?.nodeValue
                    ?: throw IllegalArgumentException("Mapache.xml file not valid")

            name to type
        }
    }

    companion object {
        private const val DEBUG = false
    }
}