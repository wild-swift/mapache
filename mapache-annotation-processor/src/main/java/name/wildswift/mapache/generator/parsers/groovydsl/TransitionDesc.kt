package name.wildswift.mapache.generator.parsers.groovydsl

import name.wildswift.mapache.generator.parsers.groovydsl.State

data class TransitionDesc(
        val beginState: State,
        val endState: State,
        val implClass: String
)
