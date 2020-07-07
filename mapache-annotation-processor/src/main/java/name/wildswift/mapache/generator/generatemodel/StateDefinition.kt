package name.wildswift.mapache.generator.generatemodel

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import name.wildswift.mapache.generator.parsers.groovy.model.StateSubGraph

data class StateDefinition(
        val viewSetClassName: TypeName,

        val stateClassName: ClassName,

        val wrapperClassName: ClassName,
        /**
         * List of constructor parameters
         */
        val parameters: List<ParameterDefinition>,

        val addToBackStack: Boolean,

        val moveDefinition: List<StateMoveDefinition>,

        val viewRootType: TypeName,

        val hasSubGraph: Boolean,

        val subGraphRootIndex: Int?,

        val subGraphInitialStateName: ClassName?
) {

}