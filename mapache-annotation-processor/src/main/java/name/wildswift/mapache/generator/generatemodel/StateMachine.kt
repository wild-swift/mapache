package name.wildswift.mapache.generator.generatemodel

data class StateMachine(
        val layers:List<StateMachineLayer>,
        val actions: List<Action>,
        val basePackageName: String,
        val eventsPackage: String,
        val statesPackage: String,
        val transitionsPackage: String,
        val diClass: String
) {
    fun states(): List<State> {
        return layers.flatMap {
            val states = mutableListOf<State>()
            addStates(it.initialState, states)
            states.toList()
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
        return states().flatMap { state ->
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