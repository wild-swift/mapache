package name.wildswift.mapache.generator.parsers.groovy

import com.squareup.javapoet.ClassName
import groovy.lang.Binding
import groovy.lang.GroovyShell
import groovy.util.DelegatingScript
import name.wildswift.mapache.generator.EmptyClassesClassLoader
import name.wildswift.mapache.generator.generatemodel.EventDefinition
import name.wildswift.mapache.generator.generatemodel.GenerateModel
import name.wildswift.mapache.generator.generatemodel.ParameterDefinition
import name.wildswift.mapache.generator.grdsl.MapacheGroovyDslDelegate
import name.wildswift.mapache.generator.parsers.ModelParser
import name.wildswift.mapache.generator.toType
import org.codehaus.groovy.control.CompilerConfiguration
import java.io.File
import javax.annotation.processing.ProcessingEnvironment

class GroovyDslParser: ModelParser {
    override fun getModel(file: File, prefix:String, modulePackageName: String, processingEnv: ProcessingEnvironment): GenerateModel {
        val emptyClassesClassLoader = EmptyClassesClassLoader(GroovyDslParser::class.java.classLoader)

        val cc = CompilerConfiguration()
        cc.scriptBaseClass = DelegatingScript::class.java.name

        val sh = GroovyShell(emptyClassesClassLoader, Binding(), cc)
        val script = sh.parse(file) as DelegatingScript

        val mapacheGroovyDslDelegate = MapacheGroovyDslDelegate()
        script.delegate = mapacheGroovyDslDelegate

        script.run()

        return mapacheGroovyDslDelegate.stateMachine.let { parseModel ->
            val events = parseModel.actions.map { EventDefinition(it.name, ClassName.get(parseModel.eventsPackage, it.name), it.params.map { ParameterDefinition(it.name, it.type.toType()) }) }
            GenerateModel(
                    eventsBasePackage = parseModel.eventsPackage,
                    baseEventClass = ClassName.get(parseModel.eventsPackage, "${prefix}Event"),
                    events = events,

                    statesBasePackage = parseModel.statesPackage,
                    baseStateWrappersClass = ClassName.get(parseModel.statesPackage, "${prefix}MState"),
                    states = parseModel.states(parseModel.statesPackage, events),
                    dependencySource = parseModel.diClass.toType(),
                    buildConfigClass = ClassName.get(modulePackageName, "BuildConfig"),

                    transitionsBasePackage = parseModel.transitionsPackage,
                    baseTransitionClass = ClassName.get(parseModel.transitionsPackage, "${prefix}StateTransition"),
                    transitions = parseModel.transitions(parseModel.statesPackage, processingEnv)
            )
        }


    }
}