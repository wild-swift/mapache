package name.wildswift.mapache.generator.parsers.groovy

import name.wildswift.mapache.generator.parsers.groovy.State
import name.wildswift.mapache.generator.parsers.groovy.StateGraphBase

data class StateMachineLayer(
        /**
         * Name of field in android R.id class
         */
        val contentId: String,
        /**
         * Link to start state
         */
        override val initialState: State,
        /**
         * Enable back stack for this layer
         */
        override val hasBackStack: Boolean
): StateGraphBase()