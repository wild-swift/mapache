package name.wildswift.mapache.generator.parsers.groovy

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import groovy.lang.Binding
import groovy.lang.GroovyShell
import groovy.util.DelegatingScript
import name.wildswift.mapache.generator.extractViewSetType
import name.wildswift.mapache.generator.extractViewTypeFromViewContent
import name.wildswift.mapache.generator.generatemodel.*
import name.wildswift.mapache.generator.parsers.ModelParser
import name.wildswift.mapache.generator.parsers.groovy.dsldelegates.MapacheGroovyDslDelegate
import name.wildswift.mapache.generator.parsers.groovy.model.State
import name.wildswift.mapache.generator.parsers.groovy.model.StateMachineLayer
import name.wildswift.mapache.generator.toType
import org.codehaus.groovy.control.CompilerConfiguration
import java.io.File
import java.text.ParseException
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.VariableElement

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
            val events = parseModel.actions.map { EventDefinition(it.name, ClassName.get(parseModel.eventsPackage, it.name), it.params.map { ParameterDefinition(it.name, TypeName.get(it.type)) }) }
            GenerateModel(
                    eventsBasePackage = parseModel.eventsPackage,
                    baseEventClass = ClassName.get(parseModel.eventsPackage, "${prefix}Event"),
                    events = events,

                    statesBasePackage = parseModel.statesPackage,
                    baseStateWrappersClass = ClassName.get(parseModel.statesPackage, "${prefix}MState"),
                    states = states(parseModel.layers, parseModel.statesPackage, events, processingEnv),
                    dependencySource = parseModel.diClass.toType(),
                    buildConfigClass = ClassName.get(modulePackageName, "BuildConfig"),

                    transitionsBasePackage = parseModel.transitionsPackage,
                    baseTransitionClass = ClassName.get(parseModel.transitionsPackage, "${prefix}StateTransition"),
                    transitionsFactoryClass = ClassName.get(parseModel.transitionsPackage, "${prefix}TransitionsFactory"),
                    emptyTransitionClass = ClassName.get(parseModel.transitionsPackage, "${prefix}EmptyTransitionWrapper"),
                    defaultTransitionClass = ClassName.get(parseModel.transitionsPackage, "${prefix}DefaultTransitionWrapper"),
                    transitions = transitions(parseModel.layers, parseModel.transitionsPackage, parseModel.statesPackage, processingEnv),

                    smUtilityClass = ClassName.get(parseModel.basePackageName, "${prefix}NavigationStateMachine"),
                    viewContentMetaSourceClass = ClassName.get(parseModel.basePackageName, "${prefix}ViewContentMetaSource"),
                    viewContents = viewContentHolders(parseModel.layers, parseModel.statesPackage, processingEnv),
                    layers = parseModel.layers.map {
                        LayerDefinition(
                                initialStateWrapperType = ClassName.get(parseModel.statesPackage, "${it.initialState.name.simpleName}Wrapper"),
                                contentIdClass = if (it.contentId == 0) ClassName.get("android", "R") else ClassName.get(modulePackageName, "R"),
                                contentId = if (it.contentId == 0)
                                    "\$T.id.content"
                                else
                                    processingEnv.elementUtils
                                            .getTypeElement(modulePackageName + ".R.id")
                                            .enclosedElements
                                            .find { (it as? VariableElement)?.constantValue == parseModel.layers[0].contentId }
                                            .let { it ?: throw ParseException("", 0) }
                                            .let { it as VariableElement }
                                            .simpleName
                                            .let {
                                                "\$T.id.$it"
                                            }
                                            .toString()
                        )
                    }
            )
        }
    }

    fun states(layers: List<StateMachineLayer>, packageName: String, events: List<EventDefinition>, processingEnv: ProcessingEnvironment): List<StateDefinition> {
        val states = layers.flatMap {
            val states = mutableListOf<State>()
            addStates(it.initialState, states)
            states.toList()
        }
        val wrapperClassMapping = states.map { it.name to ClassName.get(packageName, "${it.name.simpleName}Wrapper") }.toMap()
        return states.map {
            StateDefinition(
                    viewSetClassName = processingEnv.elementUtils.getTypeElement(it.name.canonicalName).extractViewSetType(),
                    stateClassName = ClassName.get(it.name),
                    wrapperClassName = wrapperClassMapping[it.name] ?: error("Internal error"),
                    parameters = it.parameters.orEmpty().map { ParameterDefinition(it.name, TypeName.get(it.type)) },
                    addToBackStack = it.addToBackStack,
                    moveDefenition = it.movements.mapNotNull { movment ->
                        if (movment.action != null) {
                            StateMoveDefinition(
                                    actionType = events.filter { it.name == movment.action.name }.first().typeName,
                                    moveParameters = movment.endState.parameters.orEmpty().map { ParameterDefinition(it.name, TypeName.get(it.type)) },
                                    targetStateWrapperClass = wrapperClassMapping[movment.endState.name]
                                            ?: error("Internal error")
                            )
                        } else {
                            null
                        }
                    }
            )
        }
    }

    fun viewContentHolders(layers: List<StateMachineLayer>, packageName: String, processingEnv: ProcessingEnvironment): List<ViewContentDefinition> {
        val states = layers.flatMap {
            val states = mutableListOf<State>()
            addStates(it.initialState, states)
            states.toList()
        }
        val wrapperClassMapping = states.map { it.name to ClassName.get(packageName, "${it.name.simpleName}Wrapper") }.toMap()
        return states.flatMap { state ->
            state.viewModels.map { defentition ->
                ViewContentDefinition(
                        typeName = ClassName.get(defentition.type),
                        viewType = processingEnv.elementUtils.getTypeElement(defentition.type.canonicalName).extractViewTypeFromViewContent(),
                        name = defentition.name,
                        default = defentition.default,
                        targetState = wrapperClassMapping[state.name] ?: error("Internal error")
                )
            }
        }
    }

    private fun addStates(initialState: State, states: MutableList<State>) {
        if (states.contains(initialState)) return
        states.add(initialState)
        initialState.movements
                .map { it.endState }
                .forEach {
                    addStates(it, states)
                }
        initialState.child
                ?.initialState
                ?.also {
                    addStates(it, states)
                }
    }

    fun transitions(layers: List<StateMachineLayer>, packageName: String, statesPackage: String, processingEnv: ProcessingEnvironment): List<TransitionDefinition> {
        val states = layers
                .flatMap {
                    val states = mutableListOf<State>()
                    addStates(it.initialState, states)
                    states.toList()
                }
        val wrapperClassMapping = states.map { it.name to ClassName.get(statesPackage, "${it.name.simpleName}Wrapper") }.toMap()
        val classMapping = states.map { it.name to ClassName.get(it.name) }.toMap()
        return states
                .flatMap { state ->
                    state.movements.map { movement ->
                        val fromName = state.name.simpleName.let { if (it.endsWith("State")) it.dropLast("State".length) else it }
                        val toName = movement.endState.name.simpleName.let { if (it.endsWith("State")) it.dropLast("State".length) else it }

                        val fromViewSetType = processingEnv.elementUtils.getTypeElement(state.name.canonicalName).extractViewSetType()
                        val toViewSetType = processingEnv.elementUtils.getTypeElement(movement.endState.name.canonicalName).extractViewSetType()


                        TransitionDefinition(
                                typeName = ClassName.get(movement.implClass),
                                wrapperTypeName = ClassName.get(packageName, "${fromName}To${toName}TransitionWrapper"),
                                beginViewSetClass = fromViewSetType,
                                beginStateClass = classMapping[state.name]  ?: error("Internal error"),
                                beginStateWrapperClass = wrapperClassMapping[state.name]  ?: error("Internal error"),
                                endViewSetClass = toViewSetType,
                                endStateClass = classMapping[movement.endState.name]  ?: error("Internal error"),
                                endStateWrapperClass = wrapperClassMapping[movement.endState.name]  ?: error("Internal error")
                        )
                    }
                }
    }
}