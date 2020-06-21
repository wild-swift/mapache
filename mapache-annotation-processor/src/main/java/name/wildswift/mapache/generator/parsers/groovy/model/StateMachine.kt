package name.wildswift.mapache.generator.parsers.groovy.model

import com.squareup.javapoet.ClassName
import name.wildswift.mapache.generator.extractViewSetType
import name.wildswift.mapache.generator.generatemodel.*
import name.wildswift.mapache.generator.toType
import javax.annotation.processing.ProcessingEnvironment

data class StateMachine(
        val layers: List<StateMachineLayer>,
        val actions: List<Action>,
        val basePackageName: String,
        val eventsPackage: String,
        val statesPackage: String,
        val transitionsPackage: String,
        val diClass: String
) {

}