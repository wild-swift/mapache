package name.wildswift.mapache.generator.grdsl

import name.wildswift.mapache.generator.dslmodel.Action
import name.wildswift.mapache.generator.dslmodel.Movement
import name.wildswift.mapache.generator.dslmodel.State
import name.wildswift.mapache.generator.dslmodel.StateSubGraph
import java.lang.IllegalArgumentException

class StateDelegate(private val state: State): GraphBaseDelegate() {
    private var movements: State? = null
        private set

    private var movementRules: List<Triple<String, String, String>> = listOf()

    private var sceneViewClass = ""
    private var sceneViewIndex = -1

    override fun invokeMethod(name: String, args: Array<Any>): Any? {
        if (name == "when") {
            return MovementRuleBuilder((args[0] as Class<*>).name) {
                movementRules += it
            }
        }
        if (name == "rootview") {
            if (sceneViewIndex >= 0) throw IllegalArgumentException("Multiple definition of rootview in $name")
            val (sceneViewIndex, sceneViewClass) = (args[0] as Map<Int, Class<*>>).entries.single()
            if (sceneViewIndex < 0) throw ArrayIndexOutOfBoundsException()
            this.sceneViewIndex = sceneViewIndex
            this.sceneViewClass = sceneViewClass.name
            return null
        }

        return super.invokeMethod(name, args)
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

    override fun name(): String = state.name

    fun doFinal(actions: List<Action>, states: List<State>, parentName: String) {
        state.movements = movementRules.map { (actionName, targetStateName, transitionName) ->
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