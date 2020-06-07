package name.wildswift.mapache.generator.generatemodel

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