package name.wildswift.mapache.generator.grdsl

import groovy.lang.GroovyObject
import groovy.lang.MetaClass
import name.wildswift.mapache.generator.parsers.groovy.Action
import name.wildswift.mapache.generator.parsers.groovy.Parameter
import org.codehaus.groovy.runtime.InvokerHelper
import java.lang.IllegalStateException

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

        var index = 0
        actions += Action(
                name,
                args.flatMap { pr ->
                    if (pr is Class<*>)
                        listOf(Parameter("p${index++}", pr.name))
                    else if (pr is Map<*, *>)
                        pr.entries.map {
                            Parameter(it.key as String, (it.value as Class<*>).name)
                        }
                    else
                        throw IllegalStateException("Unable to parse action $name defenition")
                }
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