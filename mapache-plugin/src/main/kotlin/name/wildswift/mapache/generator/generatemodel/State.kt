package name.wildswift.mapache.generator.generatemodel

data class State(
        /**
         * Class name of state implementation
         * Class must implements special interface
         */
        val name: String,
        /**
         * List of constructor parameters
         */
        var parameters: List<Parameter>? = null,
        /**
         * List of acceptable actions with rules
         */
        var movements: List<Movement>? = null,
        /**
         * Child state machine
         */
        var child: StateSubGraph? = null
)