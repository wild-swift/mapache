package name.wildswift.mapache.generator.parsers.groovy

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import groovy.lang.Binding
import groovy.lang.GroovyShell
import groovy.util.DelegatingScript
import name.wildswift.mapache.generator.*
import name.wildswift.mapache.generator.generatemodel.*
import name.wildswift.mapache.generator.parsers.ModelParser
import name.wildswift.mapache.generator.parsers.groovy.dsldelegates.MapacheGroovyDslDelegate
import name.wildswift.mapache.generator.parsers.groovy.model.State
import name.wildswift.mapache.generator.parsers.groovy.model.StateMachineLayer
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
            val states = mutableListOf<StateDefinition>()
            val transitions = mutableListOf<TransitionDefinition>()
            val viewContent = mutableListOf<ViewContentDefinition>()
            parseModel.layers.forEachIndexed { index, layer ->
                val rootType = layer.contentClass?.let { ClassName.get(it) } ?: frameLayoutTypeName
                addStates("L${index + 1}", parseModel.statesPackage, parseModel.transitionsPackage, layer.initialState, rootType, events, states, transitions, viewContent, processingEnv)
            }
            GenerateModel(
                    baseEventClass = ClassName.get(parseModel.eventsPackage, "${prefix}Event"),
                    events = events,

                    baseStateWrappersClass = ClassName.get(parseModel.statesPackage, "${prefix}MState"),
                    states = states,
                    dependencySource = parseModel.diClass.toType(),
                    buildConfigClass = ClassName.get(modulePackageName, "BuildConfig"),

                    baseTransitionClass = ClassName.get(parseModel.transitionsPackage, "${prefix}StateTransition"),
                    transitionsFactoryClass = ClassName.get(parseModel.transitionsPackage, "${prefix}TransitionsFactory"),
                    emptyTransitionClass = ClassName.get(parseModel.transitionsPackage, "${prefix}EmptyTransitionWrapper"),
                    defaultTransitionClass = ClassName.get(parseModel.transitionsPackage, "${prefix}DefaultTransitionWrapper"),
                    transitions = transitions,

                    smUtilityClass = ClassName.get(parseModel.basePackageName, "${prefix}NavigationStateMachine"),
                    viewContentMetaSourceClass = ClassName.get(parseModel.basePackageName, "${prefix}ViewContentMetaSource"),
                    viewContents = viewContent,
                    layers = layers(parseModel.layers, parseModel.statesPackage, modulePackageName, processingEnv)
            )
        }
    }

    private fun layers(layers: List<StateMachineLayer>, statesPackage: String, modulePackageName: String, processingEnv: ProcessingEnvironment): List<LayerDefinition> {
        return layers.mapIndexed { index, layer ->
            LayerDefinition(
                    initialStateWrapperType = generateStateWrapperName("L${index + 1}", statesPackage, layer.initialState),
                    contentIdClass = if (layer.contentId == 0) ClassName.get("android", "R") else ClassName.get(modulePackageName, "R"),
                    contentId = if (layer.contentId == 0)
                        "\$T.id.content"
                    else
                        processingEnv.elementUtils
                                .getTypeElement(modulePackageName + ".R.id")
                                .enclosedElements
                                .find { (it as? VariableElement)?.constantValue == layer.contentId }
                                .let { it ?: throw ParseException("", 0) }
                                .let { it as VariableElement }
                                .simpleName
                                .let {
                                    "\$T.id.$it"
                                }
                                .toString()
            )
        }
    }


    private fun generateStateWrapperName(prefix: String, packageName: String, it: State): ClassName {
        return ClassName.get(packageName, "$prefix${it.name.simpleName}Wrapper")
    }

    private fun addStates(prefix: String, statesPackage: String, transitionsPackage: String, state: State, rootType: TypeName, events: List<EventDefinition>, states: MutableList<StateDefinition>, transitions: MutableList<TransitionDefinition>, viewContent: MutableList<ViewContentDefinition>, processingEnv: ProcessingEnvironment) {
        if (states.find { it.wrapperClassName == generateStateWrapperName(prefix, statesPackage, state) } != null) return

        val acronym = state.name.simpleName.filter { it.isUpperCase() }
        val subGraphPrefix = "$prefix$acronym"

        states.add(StateDefinition(
                viewSetClassName = processingEnv.elementUtils.getTypeElement(state.name.canonicalName).extractViewSetType(),
                stateClassName = ClassName.get(state.name),
                wrapperClassName = generateStateWrapperName(prefix, statesPackage, state),
                parameters = state.parameters.orEmpty().map { ParameterDefinition(it.name, TypeName.get(it.type)) },
                addToBackStack = state.addToBackStack,
                singleInBackStack = state.singleInBackStack,
                moveDefinition = state.movements.mapNotNull { movment ->
                    if (movment.action != null) {
                        StateMoveDefinition(
                                actionType = events.filter { it.name == movment.action.name }.first().typeName,
                                moveParameters = movment.endState.parameters.orEmpty().map { ParameterDefinition(it.name, TypeName.get(it.type)) },
                                targetStateWrapperClass = generateStateWrapperName(prefix, statesPackage, movment.endState)
                        )
                    } else {
                        null
                    }
                },
                viewRootType = rootType,
                hasSubGraph = state.child != null,
                subGraphInitialStateName = state.child?.let { generateStateWrapperName(subGraphPrefix, statesPackage, it.initialState) },
                subGraphRootIndex = state.child?.sceneViewIndex,
                subGraphRootType = state.child?.run { sceneViewClass?.let { ClassName.get(it) } ?: rootType }
        ))

        state.movements.forEach { movement ->
            val fromName = state.name.simpleName.let { if (it.endsWith("State")) it.dropLast("State".length) else it }
            val toName = movement.endState.name.simpleName.let { if (it.endsWith("State")) it.dropLast("State".length) else it }

            val fromViewSetType = processingEnv.elementUtils.getTypeElement(state.name.canonicalName).extractViewSetType()
            val toViewSetType = processingEnv.elementUtils.getTypeElement(movement.endState.name.canonicalName).extractViewSetType()

            transitions.add(
                    TransitionDefinition(
                            typeName = ClassName.get(movement.implClass),
                            wrapperTypeName = ClassName.get(transitionsPackage, "${prefix}${fromName}To${toName}TransitionWrapper"),
                            beginViewSetClass = fromViewSetType,
                            beginStateClass = ClassName.get(state.name),
                            beginStateWrapperClass = generateStateWrapperName(prefix, statesPackage, state),
                            endViewSetClass = toViewSetType,
                            endStateClass = ClassName.get(movement.endState.name),
                            endStateWrapperClass =generateStateWrapperName(prefix, statesPackage, movement.endState),
                            viewRootType = rootType
                    )
            )

        }
        state.viewModels.forEach { defentition ->
            viewContent.add(ViewContentDefinition(
                    typeName = ClassName.get(defentition.type),
                    viewType = processingEnv.elementUtils.getTypeElement(defentition.type.canonicalName).extractViewTypeFromViewContent(),
                    name = defentition.name,
                    default = defentition.default,
                    targetState = generateStateWrapperName(prefix, statesPackage, state)
            ))
        }

        state.movements
                .map { it.endState }
                .forEach {
                    addStates(prefix, statesPackage, transitionsPackage, it, rootType, events, states, transitions, viewContent, processingEnv)
                }
        state.child
                ?.also { subGraph ->
                    val childRootType = subGraph.sceneViewClass?.let { ClassName.get(it) } ?: rootType
                    addStates(subGraphPrefix, statesPackage, transitionsPackage, subGraph.initialState, childRootType, events, states, transitions, viewContent, processingEnv)
                }
    }
}