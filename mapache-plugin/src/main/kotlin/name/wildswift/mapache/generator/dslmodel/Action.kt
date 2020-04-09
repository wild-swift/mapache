package name.wildswift.mapache.generator.dslmodel

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