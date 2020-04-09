package name.wildswift.mapache.generator.dslmodel

abstract class StateGraphBase {
    /**
     * Link to start state
     */
    abstract val initialState: State
    /**
     * Enable back stack for this layer
     */
    abstract val hasBackStack: Boolean

}