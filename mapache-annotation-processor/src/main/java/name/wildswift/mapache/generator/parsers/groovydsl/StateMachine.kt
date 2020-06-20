package name.wildswift.mapache.generator.parsers.groovydsl

import com.squareup.javapoet.ClassName
import name.wildswift.mapache.generator.generatemodel.EventDefinition
import name.wildswift.mapache.generator.generatemodel.ParameterDefinition
import name.wildswift.mapache.generator.generatemodel.StateDefinition
import name.wildswift.mapache.generator.generatemodel.StateMoveDefinition
import name.wildswift.mapache.generator.toType

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

    fun transitions(): List<TransitionDesc> {
        return layers
                .flatMap {
                    val states = mutableListOf<State>()
                    addStates(it.initialState, states)
                    states.toList()
                }
                .flatMap { state ->
                    state.movements.map { movement ->
                        TransitionDesc(
                                beginState = state,
                                endState = movement.endState,
                                implClass = movement.implClass
                        )
                    }
                }
    }
}