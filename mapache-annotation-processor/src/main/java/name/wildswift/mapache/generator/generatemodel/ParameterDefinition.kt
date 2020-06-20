package name.wildswift.mapache.generator.generatemodel

import com.squareup.javapoet.TypeName

data class ParameterDefinition(
        /**
         * Parameter name
         */
        val name:String,
        /**
         * class name
         */
        val type: TypeName
)