package name.wildswift.mapache.generator.parsers.groovy

import name.wildswift.mapache.generator.parsers.groovy.State

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