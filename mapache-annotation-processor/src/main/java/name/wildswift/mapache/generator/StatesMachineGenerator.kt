package name.wildswift.mapache.generator

import name.wildswift.mapache.config.ConfigType
import name.wildswift.mapache.config.GenerateNavigation
import name.wildswift.mapache.generator.parsers.ModelParser
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.QualifiedNameable
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic
import javax.tools.StandardLocation

@SupportedAnnotationTypes("name.wildswift.mapache.config.GenerateNavigation")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedOptions("mapache.configs.location")
class StatesMachineGenerator: AbstractProcessor() {


    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        val configurations = roundEnv.getElementsAnnotatedWith(GenerateNavigation::class.java).flatMap {
            if (it.kind != ElementKind.CLASS) {
                processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "Can be applied only to class.")
                throw IllegalArgumentException("GenerateNavigation may be used only as class annotation. Can't apply to ${(it as? QualifiedNameable)?.qualifiedName
                        ?: it.simpleName}")
            }

            it.getAnnotationsByType(GenerateNavigation::class.java).toList()
        }
        if (configurations.any { it.value.isEmpty() }) {
            processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "Prefix must not be empty")
            throw IllegalArgumentException("GenerateNavigation must specify not empty prefix (value).")
        }
        if (configurations.size != configurations.map { it.value }.toSet().size) {
            processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "Duplicate prefix detected")
            throw IllegalArgumentException("Each GenerateNavigation must have unique prefix (value). Duplication detected.")
        }

        configurations.forEach {
            generateStateMachine(it.value, it.configName, it.type)
        }

//        processingEnv.filer.createSourceFile()
        return true
    }

    private fun generateStateMachine(value: String, configName: String, type: ConfigType) {
        val parser = ModelParser.getInstance(type)
        val options = processingEnv.options.entries.joinToString { (name, value) -> "$name = $value" }
//        val file = File(".").toURI()
        val file = processingEnv.filer.getResource(StandardLocation.SOURCE_PATH, "", "test.txt").toUri();
        throw IllegalArgumentException("$file")

        // parser.getModel(file)

    }
}