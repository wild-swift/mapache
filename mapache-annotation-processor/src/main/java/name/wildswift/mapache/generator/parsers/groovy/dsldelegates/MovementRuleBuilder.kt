package name.wildswift.mapache.generator.parsers.groovy.dsldelegates

class MovementRuleBuilder(private val actionName: String, private val callback: (Triple<String, Class<*>, Class<*>>) -> Unit) {
    private var targetStateName: Class<*>? = null

    fun go(clazz: Class<*>) : MovementRuleBuilder {
        targetStateName = clazz
        return this
    }

    fun with(clazz: Class<*>) {
        callback(Triple<String, Class<*>, Class<*>>(actionName, targetStateName ?: error("Target not set for move"), clazz))
    }
}