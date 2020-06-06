package name.wildswift.mapache.generator.grdsl

import groovy.lang.Closure
import groovy.lang.GroovyObject
import groovy.lang.MetaClass
import name.wildswift.mapache.generator.generatemodel.Action
import name.wildswift.mapache.generator.generatemodel.State
import name.wildswift.mapache.generator.generatemodel.StateGraphBase
import org.codehaus.groovy.runtime.InvokerHelper
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

@Suppress("SuspiciousCollectionReassignment")
abstract class GraphBaseDelegate : GroovyObject {
    var hasBackStack = true

    protected var initialRaw: Pair<State, Closure<*>>? = null
        private set
    private var elementsRaw = listOf<Pair<State, Closure<*>>>()
    private var allClosure: Closure<*>? = null


    override fun invokeMethod(name: String, inArgs: Any?): Any? {
        val args = inArgs as? Array<Any?> ?: return null
        return invokeMethod(name, args)
    }

    protected open fun invokeMethod(name: String, args: Array<Any?>): Any? {
        throw IllegalArgumentException("Unknown command $name with parameters [${args.joinToString { if (it != null) it::class.simpleName.orEmpty() else "null" }}] in ${name()}")
    }

    fun all(closure: Closure<*>) {
        allClosure = closure
    }

    fun `$`(stateClass: Class<*>, initializer: Closure<*>) {
        elementsRaw += State(name = stateClass.name) to initializer
    }

    fun from(stateClass: Class<*>, initializer: Closure<*>) {
        if (initialRaw != null) throw IllegalStateException()
        initialRaw = (State(name = stateClass.name) to initializer).also {
            elementsRaw += it
        }
    }

    fun hasBackStack(value: Boolean) {
        hasBackStack = value
    }

    override fun setProperty(propertyName: String, newValue: Any?) {
        throw IllegalArgumentException("Unknown command $propertyName with parameters [${if (newValue != null) newValue::class.simpleName else "null"}] in ${name()}")
    }

    override fun getProperty(propertyName: String): Any? {
        throw IllegalArgumentException("Unknown command $propertyName with parameters [] in ${name()}")
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
        val additionalMovments = allClosure?.let { allClosure ->
            val allDelegate = AllDelegate()
            allClosure.delegate = allDelegate
            allClosure.resolveStrategy = Closure.DELEGATE_FIRST
            allClosure.call()
            allDelegate.movementRules
        }
        if (elementsRaw.map { (state, _) -> state.name }.let { it.size != it.toSet().size }) throw IllegalArgumentException("Names not unique for ${name()}")
        val states = elementsRaw.map { (state, _) -> state}
        elementsRaw.forEach { (state, closure) ->
            val stateDelegate = StateDelegate(state)
            closure.delegate = stateDelegate
            closure.resolveStrategy = Closure.DELEGATE_FIRST
            closure.call()
            if (additionalMovments != null) {
                stateDelegate.movementRules += additionalMovments
            }
            stateDelegate.doFinal(actions, states, this.name())
        }
    }
}