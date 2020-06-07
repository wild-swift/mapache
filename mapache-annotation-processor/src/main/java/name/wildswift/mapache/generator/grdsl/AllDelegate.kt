package name.wildswift.mapache.generator.grdsl

import groovy.lang.GroovyObject
import groovy.lang.MetaClass
import name.wildswift.mapache.generator.generatemodel.Action
import name.wildswift.mapache.generator.generatemodel.Movement
import name.wildswift.mapache.generator.generatemodel.State
import org.codehaus.groovy.runtime.InvokerHelper
import java.lang.IllegalArgumentException

class AllDelegate() : GroovyObject {
    private var movements: List<Movement>? = null
        private set

    var movementRules: List<Triple<String, String, String>> = listOf()

    override fun invokeMethod(name: String, inArgs: Any?): Any? {
        val args = inArgs as? Array<Any> ?: return null
        if (name == "when") {
            return MovementRuleBuilder((args[0] as Class<*>).name) {
                movementRules += it
            }
        }

        if (name == "go") {
            return MovementRuleBuilder("") {
                movementRules += it
            }.go(args[0] as Class<*>)
        }
        println("invokeMethod ${name} : ${args.toList()}")
        return null
    }

    override fun setProperty(propertyName: String, newValue: Any?) {
        println("setProperty $propertyName = $newValue")

    }

    override fun getProperty(propertyName: String): Any? {
        println("getProperty ${propertyName}")
        return null
    }

    private var metaClass: MetaClass = InvokerHelper.getMetaClass(javaClass)

    override fun setMetaClass(metaClass: MetaClass) {
        this.metaClass = metaClass
    }

    override fun getMetaClass(): MetaClass {
        return metaClass
    }

    fun doFinal(actions: List<Action>, states: List<State>, parentName: String) {
        movements = movementRules.filter { it.first.isNotBlank() }.map { (actionName, targetStateName, transitionName) ->
            val targetState = states.find { it.name == targetStateName } ?: throw IllegalArgumentException("State $targetStateName not found in $parentName")
            val action = actions.find { it.name == actionName } ?: throw IllegalArgumentException("Action $actionName not found")
            if (targetState.parameters == null) {
                targetState.parameters = action.params
            }

            if (targetState.parameters?.size != action.params.size ||
                    action.params.filterIndexed { index, parameter -> targetState.parameters?.get(index)?.type != parameter.type }.any()) throw IllegalArgumentException("State $targetState cann't be run by action $actionName")

            Movement(action, targetState, transitionName)
        }
    }

}