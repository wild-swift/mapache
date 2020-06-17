package name.wildswift.mapache.generator.generatemodel

data class TransitionDesc(
    val beginState: State,
    val endState: State,
    val implClass: String
)
