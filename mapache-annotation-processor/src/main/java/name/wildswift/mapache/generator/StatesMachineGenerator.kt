package name.wildswift.mapache.generator

import name.wildswift.mapache.config.ConfigType
import name.wildswift.mapache.config.GenerateNavigation
import name.wildswift.mapache.generator.codegen.ActionsGenerator
import name.wildswift.mapache.generator.codegen.StatesWrapperGenerator
import name.wildswift.mapache.generator.codegen.TransitionsWrapperGenerator
import name.wildswift.mapache.generator.parsers.ModelParser
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.QualifiedNameable
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@SupportedAnnotationTypes("name.wildswift.mapache.config.GenerateNavigation")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedOptions("mapache.configs.location", "application.id")
class StatesMachineGenerator : AbstractProcessor() {


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

    private fun generateStateMachine(prefix: String, configName: String, type: ConfigType) {
        val parser = ModelParser.getInstance(type)
        val configsLocation = processingEnv.options["mapache.configs.location"]?.takeIf { it.isNotEmpty() }
                ?: let {
                    processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "Argument 'mapache.configs.location' is not set")
                    throw IllegalArgumentException("Argument 'mapache.configs.location' is not set")
                }
        val modulePackageName = processingEnv.options["application.id"]?.takeIf { it.isNotEmpty() }
                ?: let {
                    processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "Argument 'application.id' is not set")
                    throw IllegalArgumentException("Argument 'application.id' is not set")
                }
        val configFile = File(configsLocation)
                .resolve(configName + when (type) {
                    ConfigType.GROOVY -> ".groovy"
                    ConfigType.XML -> ".xml"
                })
                .let {
                    if (it.exists())
                        it
                    else {
                        processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "Config not found: ${it.path}")
                        throw IllegalArgumentException("Config not found: ${it.path}")
                    }
                }

        val generationModel = parser.getModel(configFile, prefix, modulePackageName, processingEnv)

        ActionsGenerator(generationModel.eventsBasePackage, generationModel.baseEventClass, generationModel.events, processingEnv.filer).generateAll()
        StatesWrapperGenerator(generationModel.statesBasePackage, generationModel.baseStateWrappersClass, generationModel.baseEventClass, generationModel.dependencySource, generationModel.buildConfigClass, generationModel.states, processingEnv).generateAll()
        TransitionsWrapperGenerator(generationModel.baseTransitionClass, generationModel.emptyTransitionClass, generationModel.baseEventClass, generationModel.baseStateWrappersClass, generationModel.dependencySource, generationModel.transitions, processingEnv.filer).generateAll()
        // parser.getModel(file)

    }
}