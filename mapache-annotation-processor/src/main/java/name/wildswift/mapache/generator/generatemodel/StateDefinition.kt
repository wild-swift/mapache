package name.wildswift.mapache.generator.generatemodel

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName

data class StateDefinition(
        val viewSetClassName: TypeName,

        val stateClassName: ClassName,

        val wrapperClassName: ClassName,
        /**
         * List of constructor parameters
         */
        val parameters: List<ParameterDefinition>,

        val moveDefenition: List<StateMoveDefinition>
) {

}