package name.wildswift.mapache.generator.parsers.groovy.model

data class TransitionDesc(
        val beginState: State,
        val endState: State,
        val implClass: String
)
