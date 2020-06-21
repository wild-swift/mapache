package name.wildswift.mapache.generator.parsers.groovy.model

data class StateMachine(
        val layers: List<StateMachineLayer>,
        val actions: List<Action>,
        val basePackageName: String,
        val eventsPackage: String,
        val statesPackage: String,
        val transitionsPackage: String,
        val diClass: String
) {

}