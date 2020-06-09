package name.wildswift.mapache.generator.generatemodel

data class StateMachine(
        val layers:List<StateMachineLayer>,
        val actions: List<Action>,
        val basePackageName: String,
        val eventsPackage: String,
        val statesPackage: String
)