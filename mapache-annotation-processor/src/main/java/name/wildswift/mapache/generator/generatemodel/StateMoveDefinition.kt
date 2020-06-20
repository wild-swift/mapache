package name.wildswift.mapache.generator.generatemodel

import com.squareup.javapoet.ClassName

data class StateMoveDefinition(
        val actionType: ClassName,
        val targetStateWrapperClass: ClassName,
        val moveParameters: List<ParameterDefinition>
) {
}