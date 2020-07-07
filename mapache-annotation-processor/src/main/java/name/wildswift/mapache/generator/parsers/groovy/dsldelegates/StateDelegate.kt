package name.wildswift.mapache.generator.parsers.groovy.dsldelegates

import name.wildswift.mapache.generator.parsers.groovy.model.*

class StateDelegate(private val state: State): GraphBaseDelegate() {
    var movementRules: List<Triple<String, Class<*>, Class<*>>> = listOf()

    var singleInBackStack = false
    var addToBackStack = true

    fun `when`(actionClass: Class<*>): MovementRuleBuilder {
        return MovementRuleBuilder(actionClass.name) {
            movementRules += it
        }
    }

    fun go(targetStateClass: Class<*>): MovementRuleBuilder {
        return MovementRuleBuilder("") {
            movementRules += it
        }.go(targetStateClass)
    }



    fun singleInBackStack(value: Boolean) {
        singleInBackStack = value
    }

    fun addToBackStack(value: Boolean) {
        addToBackStack = value
    }

    fun content(vararg clazz: Class<*>) {
        state.viewModels += clazz.map { ViewModelHolderDef(null, it, false) }
    }

    fun content(values: Map<String, Class<*>>) {
        state.viewModels += values.map { ViewModelHolderDef(it.key, it.value, false) }
    }


    override fun buildStateGraph(): StateSubGraph? {
        return initialRaw?.let { (initialState, _) ->
            StateSubGraph(
                    sceneViewClass = rootViewClass.takeIf { rootView > 0 },
                    sceneViewIndex = rootView.takeIf { it > 0 } ?: 0,
                    initialState = initialState,
                    hasBackStack = hasBackStack
            )
        }
    }

    override fun name(): String = state.name.simpleName

    fun doFinal(actions: List<Action>, states: List<State>, parentName: String) {
        state.movements = movementRules.map { (actionName, targetStateName, transitionName) ->
            val targetState = states.find { it.name == targetStateName } ?: throw IllegalArgumentException("State $targetStateName not found in $parentName")
            val action = if (actionName.isBlank()) null else actions.find { it.name == actionName } ?: throw IllegalArgumentException("Action $actionName not found")
            if (action != null) {
                if (targetState.parameters == null) {
                    targetState.parameters = action.params
                }
                if (targetState.parameters?.size != action.params.size ||
                        action.params.filterIndexed { index, parameter -> targetState.parameters?.get(index)?.type != parameter.type }.any()) throw IllegalArgumentException("State $targetState cann't be run by action $actionName")
            }

            Movement(action, targetState, transitionName)
        }

        doFinal(actions)

        state.child = buildStateGraph()
        state.addToBackStack = addToBackStack
    }

}