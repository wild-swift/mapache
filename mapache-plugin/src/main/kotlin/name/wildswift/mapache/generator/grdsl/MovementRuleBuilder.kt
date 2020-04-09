package name.wildswift.mapache.generator.grdsl

class MovementRuleBuilder(private val actionName: String, private val callback: (Triple<String, String, String>) -> Unit) {
    private var targetStateName: String = ""

    fun go(clazz: Class<*>) : MovementRuleBuilder {
        targetStateName = clazz.name
        return this
    }

    fun with(clazz: Class<*>) {
        callback(Triple<String, String, String>(actionName, targetStateName, clazz.name))
    }
}