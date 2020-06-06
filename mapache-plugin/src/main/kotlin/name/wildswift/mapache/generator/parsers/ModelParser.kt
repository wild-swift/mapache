package name.wildswift.mapache.generator.parsers

import groovy.lang.Binding
import groovy.lang.GroovyShell
import groovy.util.DelegatingScript
import name.wildswift.mapache.generator.EmptyClassesClassLoader
import name.wildswift.mapache.generator.Test
import name.wildswift.mapache.generator.generatemodel.StateMachine
import name.wildswift.mapache.generator.grdsl.MapacheGroovyDslDelegate
import org.codehaus.groovy.control.CompilerConfiguration
import java.io.File

interface ModelParser {

    fun getModel(file: File) : StateMachine

    companion object {
        fun getInstance(file: File) : ModelParser {
            return GroovyDslParser()
        }
    }
}