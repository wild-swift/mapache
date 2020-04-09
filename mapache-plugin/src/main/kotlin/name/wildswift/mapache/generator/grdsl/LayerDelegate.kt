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

class LayerDelegate : GraphBaseDelegate() {

    override fun invokeMethod(name: String, args: Array<Any>): Any? {
        return super.invokeMethod(name, args)
    }

    override fun setProperty(propertyName: String, newValue: Any?) {
        super.setProperty(propertyName, newValue)
    }

    override fun getProperty(propertyName: String): Any? {
        return super.getProperty(propertyName)
    }

    override fun buildStateGraph() : StateMachineLayer {
        return StateMachineLayer(
                "",
                initialRaw?.first ?: throw IllegalArgumentException("No root for layer"),
                hasBackStack
        )
    }

    override fun name() = "Root Layer"
}