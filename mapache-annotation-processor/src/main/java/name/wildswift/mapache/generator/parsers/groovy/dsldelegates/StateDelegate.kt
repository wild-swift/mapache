package name.wildswift.mapache.generator.parsers.groovy.dsldelegates

import name.wildswift.mapache.generator.parsers.groovy.model.Action
import name.wildswift.mapache.generator.parsers.groovy.model.Movement
import name.wildswift.mapache.generator.parsers.groovy.model.State
import name.wildswift.mapache.generator.parsers.groovy.model.StateSubGraph

class StateDelegate(private val state: State): GraphBaseDelegate() {
    var movementRules: List<Triple<String, Class<*>, Class<*>>> = listOf()

    private var sceneViewClass = ""
    private var sceneViewIndex = -1
    var singleInBackStack = false

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

    fun rootView(definition: Map<Int, Class<*>>) {
        val entries = definition.entries.takeIf { it.size == 1 } ?: throw IllegalArgumentException("Incorrect definition of rootView in ${name()}")
        if (sceneViewIndex >= 0) throw IllegalArgumentException("Multiple definition of rootView in ${name()}")
        val (sceneViewIndex, sceneViewClass) = entries.single()
        if (sceneViewIndex < 0) throw ArrayIndexOutOfBoundsException()
        this.sceneViewIndex = sceneViewIndex
        this.sceneViewClass = sceneViewClass.name
    }

    fun singleInBackStack(value: Boolean) {
        singleInBackStack = value
    }


    override fun buildStateGraph(): StateSubGraph? {
        return initialRaw?.let { (initialState, _) ->
            StateSubGraph(
                    sceneViewClass = sceneViewClass,
                    sceneViewIndex = sceneViewIndex,
                    initialState = initialState,
                    hasBackStack = hasBackStack
            )
        }
    }

    override fun name(): String = state.name.simpleName

    fun doFinal(actions: List<Action>, states: List<State>, parentName: String) {
        state.movements = movementRules.filter { it.first.isNotBlank() }.map { (actionName, targetStateName, transitionName) ->
            val targetState = states.find { it.name == targetStateName } ?: throw IllegalArgumentException("State $targetStateName not found in $parentName")
            val action = actions.find { it.name == actionName } ?: throw IllegalArgumentException("Action $actionName not found")
            if (targetState.parameters == null) {
                targetState.parameters = action.params
            }

            if (targetState.parameters?.size != action.params.size ||
                    action.params.filterIndexed { index, parameter -> targetState.parameters?.get(index)?.type != parameter.type }.any()) throw IllegalArgumentException("State $targetState cann't be run by action $actionName")

            Movement(action, targetState, transitionName)
        }

        doFinal(actions)

        state.child = buildStateGraph()
    }

}