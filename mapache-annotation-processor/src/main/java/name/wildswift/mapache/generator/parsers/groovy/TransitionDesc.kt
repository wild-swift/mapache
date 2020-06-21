package name.wildswift.mapache.generator.parsers.groovy

import name.wildswift.mapache.generator.parsers.groovy.State

data class TransitionDesc(
        val beginState: State,
        val endState: State,
        val implClass: String
)
