package name.wildswift.mapache.generator.dslmodel

data class Movement(
        /**
         * Action that starts from
         */
        val action: Action,
        /**
         * target sate
         */
        val endState: State,
        /**
         * Class name of transition (code reference)
         */
        val implClass: String
)