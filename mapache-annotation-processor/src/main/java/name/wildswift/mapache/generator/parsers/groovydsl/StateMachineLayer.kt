package name.wildswift.mapache.generator.parsers.groovydsl

import name.wildswift.mapache.generator.parsers.groovydsl.State
import name.wildswift.mapache.generator.parsers.groovydsl.StateGraphBase

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