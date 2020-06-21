package name.wildswift.mapache.generator.parsers.groovy.model

import name.wildswift.mapache.generator.parsers.groovy.model.State

data class TransitionDesc(
        val beginState: State,
        val endState: State,
        val implClass: String
)
