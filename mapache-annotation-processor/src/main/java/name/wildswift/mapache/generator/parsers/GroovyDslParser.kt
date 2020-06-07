package name.wildswift.mapache.generator.parsers

import groovy.lang.Binding
import groovy.lang.GroovyShell
import groovy.util.DelegatingScript
import name.wildswift.mapache.generator.EmptyClassesClassLoader
import name.wildswift.mapache.generator.generatemodel.StateMachine
import name.wildswift.mapache.generator.grdsl.MapacheGroovyDslDelegate
import org.codehaus.groovy.control.CompilerConfiguration
import java.io.File

class GroovyDslParser: ModelParser {
    override fun getModel(file: File): StateMachine {
        val emptyClassesClassLoader = EmptyClassesClassLoader(GroovyDslParser::class.java.classLoader)

        val cc = CompilerConfiguration()
        cc.scriptBaseClass = DelegatingScript::class.java.name

        val sh = GroovyShell(emptyClassesClassLoader, Binding(), cc)
        val script = sh.parse(file) as DelegatingScript

        val mapacheGroovyDslDelegate = MapacheGroovyDslDelegate()
        script.delegate = mapacheGroovyDslDelegate

        script.run()

        return mapacheGroovyDslDelegate.stateMachine

    }
}