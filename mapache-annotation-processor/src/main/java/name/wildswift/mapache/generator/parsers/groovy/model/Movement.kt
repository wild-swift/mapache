package name.wildswift.mapache.generator.parsers.groovy.model

data class Movement(
        /**
         * Action that starts from
         */
        val action: Action?,
        /**
         * target sate
         */
        val endState: State,
        /**
         * Class name of transition (code reference)
         */
        val implClass: Class<*>
)