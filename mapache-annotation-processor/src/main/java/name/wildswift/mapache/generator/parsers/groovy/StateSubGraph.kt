package name.wildswift.mapache.generator.parsers.groovy

import name.wildswift.mapache.generator.parsers.groovy.State
import name.wildswift.mapache.generator.parsers.groovy.StateGraphBase

data class StateSubGraph(
        /**
         * Scene root view class
         */
        val sceneViewClass: String,
        /**
         * Index of view object in current scene
         */
        val sceneViewIndex: Int,
        /**
         * Link to start state
         */
        override val initialState: State,
        /**
         * Enable back stack for this graph
         */
        override val hasBackStack: Boolean
) : StateGraphBase()