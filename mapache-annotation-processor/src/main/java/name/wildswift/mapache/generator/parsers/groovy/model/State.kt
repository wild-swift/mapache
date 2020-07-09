package name.wildswift.mapache.generator.parsers.groovy.model

data class State(
        /**
         * Class name of state implementation
         * Class must implements special interface
         */
        val name: Class<*>,

        var addToBackStack: Boolean = true,

        var singleInBackStack: Boolean = false,
        /**
         * List of constructor parameters
         */
        var parameters: List<Parameter>? = null,
        /**
         * Child state machine
         */
        var child: StateSubGraph? = null,

        var viewModels: Set<ViewModelHolderDef> = setOf()
) {
        /**
         * List of acceptable actions with rules
         */
        var movements: List<Movement> = listOf()
}