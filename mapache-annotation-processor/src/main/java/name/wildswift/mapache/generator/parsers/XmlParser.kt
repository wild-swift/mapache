package name.wildswift.mapache.generator.parsers

import name.wildswift.mapache.generator.generatemodel.GenerateModel
import name.wildswift.mapache.generator.parsers.groovy.StateMachine
import java.io.File
import javax.annotation.processing.ProcessingEnvironment

class XmlParser: ModelParser {
    override fun getModel(file: File, prefix:String, modulePackageName: String, processingEnv: ProcessingEnvironment): GenerateModel {
        TODO("implement")
    }
}