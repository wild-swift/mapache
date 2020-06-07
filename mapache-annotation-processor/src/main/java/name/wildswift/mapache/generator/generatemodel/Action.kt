package name.wildswift.mapache.generator.generatemodel

data class Action(
        /**
         * Action name (generated class)
         */
        val name: String,
        /**
         * List of constructor parameters
         */
        val params: List<Parameter>
)