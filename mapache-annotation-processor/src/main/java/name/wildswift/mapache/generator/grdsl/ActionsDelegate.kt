package name.wildswift.mapache.generator.grdsl

import groovy.lang.GroovyObject
import groovy.lang.MetaClass
import name.wildswift.mapache.generator.generatemodel.Action
import name.wildswift.mapache.generator.generatemodel.Parameter
import org.codehaus.groovy.runtime.InvokerHelper

class ActionsDelegate : GroovyObject {
    var packageName: String = ".events"
        private set
    var actions = listOf<Action>()
        private set

    override fun invokeMethod(name: String, inArgs: Any?): Any? {
        val args = inArgs as? Array<out Any> ?: return null

        if (name == "packageName") {
            packageName = args.firstOrNull()?.toString().orEmpty()
            return null
        }

        actions += Action(
                name,
                args.map { it as Class<*> }
                        .mapIndexed { i, pr -> Parameter("p${i + 1}", pr.name) }
        )

        return null
    }

    override fun setProperty(propertyName: String, newValue: Any?) {
        if (propertyName == "packageName") {
            packageName = newValue?.toString() ?: ""
            return
        }
        println("Unknown property $propertyName")
    }

    override fun getProperty(propertyName: String): Any? {
        if (propertyName == "packageName") {
            return packageName
        }
        return null
    }

    private var metaClass: MetaClass = InvokerHelper.getMetaClass(javaClass)

    override fun setMetaClass(metaClass: MetaClass) {
        this.metaClass = metaClass
    }

    override fun getMetaClass(): MetaClass {
        return metaClass
    }
}