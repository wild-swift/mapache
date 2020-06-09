package name.wildswift.mapache.generator

import groovy.lang.Binding
import groovy.lang.GroovyShell
import groovy.util.DelegatingScript
import name.wildswift.mapache.generator.grdsl.MapacheGroovyDslDelegate
import name.wildswift.mapache.generator.generatemodel.State
import org.codehaus.groovy.control.CompilerConfiguration
import java.io.File
import java.util.*


class Test {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val emptyClassesClassLoader = EmptyClassesClassLoader(Test::class.java.classLoader)

            val cc = CompilerConfiguration()
            cc.scriptBaseClass = DelegatingScript::class.java.name

            val sh = GroovyShell(emptyClassesClassLoader, Binding(), cc)
            val script = sh.parse(File("D:\\Projects\\Android\\Mapache\\test-app\\mapache.groovy")) as DelegatingScript

            val mapacheGroovyDslDelegate = MapacheGroovyDslDelegate()
            script.delegate = mapacheGroovyDslDelegate

            script.run()

            val stateMachine = mapacheGroovyDslDelegate.stateMachine

//            println(stateMachine)

            val queue: Queue<State> = LinkedList()

            stateMachine.layers.forEach {
//                it.initialState =
            }


        }
    }
}