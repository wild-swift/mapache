package name.wildswift.mapache.generator.parsers.groovy.model

data class StateMachineLayer(
        /**
         * Name of field in android R.id class
         */
        val contentId: Int,
        /**
         * Name of field in android R.id class
         */
        val contentClass: Class<*>?,
        /**
         * Link to start state
         */
        override val initialState: State,
        /**
         * Enable back stack for this layer
         */
        override val hasBackStack: Boolean
): StateGraphBase()