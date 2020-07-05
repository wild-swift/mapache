package name.wildswift.mapache.generator.parsers

import name.wildswift.mapache.config.ConfigType
import name.wildswift.mapache.generator.generatemodel.GenerateModel
import name.wildswift.mapache.generator.parsers.groovy.GroovyDslParser
import name.wildswift.mapache.generator.parsers.xml.XmlParser
import java.io.File
import java.text.ParseException
import javax.annotation.processing.ProcessingEnvironment

interface ModelParser {

    @Throws(ParseException::class)
    fun getModel(file: File, prefix:String, modulePackageName: String, processingEnv: ProcessingEnvironment) : GenerateModel

    companion object {
        fun getInstance(type: ConfigType) : ModelParser {
            when(type){
                ConfigType.GROOVY -> return GroovyDslParser()
                ConfigType.XML -> return XmlParser()
            }

        }
    }
}