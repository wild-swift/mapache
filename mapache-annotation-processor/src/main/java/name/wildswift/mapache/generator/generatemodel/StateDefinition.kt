package name.wildswift.mapache.generator.generatemodel

import com.squareup.javapoet.ClassName

data class StateDefinition(
        /**
         * Class name of state implementation
         * Class must implements special interface
         */
        val name: String,

        val wrapperClassName: ClassName,
        /**
         * List of constructor parameters
         */
        val parameters: List<ParameterDefinition>,

        val moveDefenition: List<StateMoveDefinition>
)