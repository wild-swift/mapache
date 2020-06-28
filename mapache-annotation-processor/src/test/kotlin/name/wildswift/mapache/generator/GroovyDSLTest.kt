package name.wildswift.mapache.generator

import groovy.lang.Binding
import groovy.lang.GroovyShell
import groovy.util.DelegatingScript
import name.wildswift.mapache.generator.parsers.groovy.EmptyClassesClassLoader
import name.wildswift.mapache.generator.parsers.groovy.dsldelegates.MapacheGroovyDslDelegate
import org.codehaus.groovy.control.CompilerConfiguration
import org.junit.Assert
import org.junit.Test
import java.io.File


class GroovyDSLTest {

    @Test
    fun baseTest() {
        val emptyClassesClassLoader = EmptyClassesClassLoader(GroovyDSLTest::class.java.classLoader)

        val cc = CompilerConfiguration()
        cc.scriptBaseClass = DelegatingScript::class.java.name

        val sh = GroovyShell(emptyClassesClassLoader, Binding(), cc)
        val script = sh.parse(File("template/groovy/mapache.groovy")) as DelegatingScript

        val mapacheGroovyDslDelegate = MapacheGroovyDslDelegate()
        script.delegate = mapacheGroovyDslDelegate

        script.run()

        val stateMachine = mapacheGroovyDslDelegate.stateMachine

        Assert.assertEquals(5, stateMachine.actions.size)
        Assert.assertEquals(1, stateMachine.layers.size)
    }
}