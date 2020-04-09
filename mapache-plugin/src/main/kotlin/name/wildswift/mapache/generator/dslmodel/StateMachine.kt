package name.wildswift.mapache.generator.dslmodel

data class StateMachine(
        val layers:List<StateMachineLayer>,
        val basePackageName: String,
        val eventsPackage: String,
        val statesPackage: String
)