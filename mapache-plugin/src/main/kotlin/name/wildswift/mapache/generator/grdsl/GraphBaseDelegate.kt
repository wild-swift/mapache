package name.wildswift.mapache.generator.grdsl

import groovy.lang.Closure
import groovy.lang.GroovyObject
import groovy.lang.MetaClass
import name.wildswift.mapache.generator.dslmodel.Action
import name.wildswift.mapache.generator.dslmodel.State
import name.wildswift.mapache.generator.dslmodel.StateGraphBase
import name.wildswift.mapache.generator.dslmodel.StateMachineLayer
import org.codehaus.groovy.runtime.InvokerHelper
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

abstract class GraphBaseDelegate : GroovyObject {
    protected var hasBackStack = true
        private set

    protected var initialRaw: Pair<State, Closure<*>>? = null
        private set
    private var elementsRaw = listOf<Pair<State, Closure<*>>>()


    override fun invokeMethod(name: String, inArgs: Any?): Any? {
        val args = inArgs as? Array<Any> ?: return null
        return invokeMethod(name, args)
    }

    protected open fun invokeMethod(name: String, args: Array<Any>): Any? {

        if (name == "$") {
            elementsRaw += args.let { State(name = (it[0] as Class<*>).name) to it[1] as Closure<*> }
            return null
        }
        if (name == "from") {
            if (initialRaw != null) throw IllegalStateException()
            initialRaw = args.let { State(name = (it[0] as Class<*>).name, parameters = listOf()) to it[1] as Closure<*> }
            elementsRaw += initialRaw!!
            return null
        }

        if (name == "hasBackStack") {
            hasBackStack = args[0] as Boolean
            return null
        }

        println("invokeMethod ${name} : ${args.toList()}")
        return null
    }

    override fun setProperty(propertyName: String, newValue: Any?) {
        if (propertyName == "hasBackStack") {
            hasBackStack = newValue as Boolean
            return
        }
        println("setProperty $propertyName = $newValue")

    }

    override fun getProperty(propertyName: String): Any? {
        if (propertyName == "hasBackStack") {
            return hasBackStack
        }
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

    abstract fun buildStateGraph(): StateGraphBase?

    abstract fun name(): String

    fun doFinal(actions: List<Action>) {
        if (elementsRaw.map { (state, _) -> state.name }.let { it.size != it.toSet().size }) throw IllegalArgumentException("Names not unique for ${name()}")
        val states = elementsRaw.map { (state, _) -> state}
        elementsRaw.forEach { (state, closure) ->
            val stateDelegate = StateDelegate(state)
            closure.delegate = stateDelegate
            closure.resolveStrategy = Closure.DELEGATE_FIRST
            closure.call()
            stateDelegate.doFinal(actions, states, this.name())
        }
    }
}