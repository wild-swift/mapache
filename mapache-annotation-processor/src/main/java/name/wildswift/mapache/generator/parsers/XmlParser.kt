package name.wildswift.mapache.generator.parsers

import name.wildswift.mapache.generator.parsers.groovydsl.StateMachine
import java.io.File

class XmlParser: ModelParser {
    override fun getModel(file: File): StateMachine {
        return StateMachine(listOf(), listOf(), "", "", "", "", Object::class.java.name)
    }
}