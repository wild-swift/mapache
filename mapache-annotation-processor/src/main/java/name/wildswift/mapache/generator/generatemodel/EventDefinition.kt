package name.wildswift.mapache.generator.generatemodel

import com.squareup.javapoet.ClassName

data class EventDefinition(
        /**
         * Action name (generated class)
         */
        val name: String,

        val typeName: ClassName,
        /**
         * List of constructor parameters
         */
        val params: List<ParameterDefinition>
)