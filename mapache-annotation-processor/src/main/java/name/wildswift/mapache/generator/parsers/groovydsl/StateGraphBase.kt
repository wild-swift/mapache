package name.wildswift.mapache.generator.parsers.groovydsl

import name.wildswift.mapache.generator.parsers.groovydsl.State

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