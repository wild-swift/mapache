package name.wildswift.mapache.generator.parsers.groovy

import com.squareup.javapoet.ClassName
import name.wildswift.mapache.generator.extractViewSetType
import name.wildswift.mapache.generator.generatemodel.*
import name.wildswift.mapache.generator.toType
import javax.annotation.processing.ProcessingEnvironment

data class StateMachine(
        val layers: List<StateMachineLayer>,
        val actions: List<Action>,
        val basePackageName: String,
        val eventsPackage: String,
        val statesPackage: String,
        val transitionsPackage: String,
        val diClass: String
) {
    fun states(packageName: String, events: List<EventDefinition>): List<StateDefinition> {
        val states = layers.flatMap {
            val states = mutableListOf<State>()
            addStates(it.initialState, states)
            states.toList()
        }
        val wrapperClassMapping = states.map { it.name to ClassName.get(packageName, "${it.name.split(".").last()}Wrapper") }.toMap()
        return states.map {
            StateDefinition(
                    name = it.name,
                    stateClassName = ClassName.bestGuess(it.name),
                    wrapperClassName = wrapperClassMapping[it.name] ?: error("Internal error"),
                    parameters = it.parameters.orEmpty().map { ParameterDefinition(it.name, it.type.toType()) },
                    moveDefenition = it.movements.map { movment ->
                        StateMoveDefinition(
                                actionType = events.filter { it.name == movment.action.name }.first().typeName,
                                moveParameters = movment.endState.parameters.orEmpty().map { ParameterDefinition(it.name, it.type.toType()) },
                                targetStateWrapperClass = wrapperClassMapping[movment.endState.name]
                                        ?: error("Internal error")
                        )
                    }
            )
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

    fun transitions(packageName: String, processingEnv: ProcessingEnvironment): List<TransitionDefinition> {
        val states = layers
                .flatMap {
                    val states = mutableListOf<State>()
                    addStates(it.initialState, states)
                    states.toList()
                }
        val wrapperClassMapping = states.map { it.name to ClassName.get(packageName, "${it.name.split(".").last()}Wrapper") }.toMap()
        val classMapping = states.map { it.name to ClassName.bestGuess(it.name) }.toMap()
        return states
                .flatMap { state ->
                    state.movements.map { movement ->
                        val fromName = state.name.split(".").last().let { if (it.endsWith("State")) it.dropLast("State".length) else it }
                        val toName = movement.endState.name.split(".").last().let { if (it.endsWith("State")) it.dropLast("State".length) else it }

                        val fromViewSetType = processingEnv.elementUtils.getTypeElement(state.name).extractViewSetType()
                        val toViewSetType = processingEnv.elementUtils.getTypeElement(movement.endState.name).extractViewSetType()


                        TransitionDefinition(
                                name = movement.implClass,
                                typeName = ClassName.bestGuess(movement.implClass),
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