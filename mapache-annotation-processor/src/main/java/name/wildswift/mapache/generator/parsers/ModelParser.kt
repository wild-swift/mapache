package name.wildswift.mapache.generator.parsers

import name.wildswift.mapache.config.ConfigType
import name.wildswift.mapache.generator.parsers.groovydsl.StateMachine
import name.wildswift.mapache.generator.parsers.groovydsl.GroovyDslParser
import java.io.File

interface ModelParser {

    fun getModel(file: File) : StateMachine

    companion object {
        fun getInstance(type: ConfigType) : ModelParser {
            when(type){
                ConfigType.GROOVY -> return GroovyDslParser()
                ConfigType.XML -> return XmlParser()
            }

        }
    }
}