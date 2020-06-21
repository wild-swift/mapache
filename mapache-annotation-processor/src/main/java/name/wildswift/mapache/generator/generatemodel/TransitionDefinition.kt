package name.wildswift.mapache.generator.generatemodel

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import name.wildswift.mapache.utils.StateWrapper

data class TransitionDefinition(
        val name: String,
        val typeName: ClassName,
        val wrapperTypeName: ClassName,
        val beginViewSetClass: TypeName,
        val beginStateClass: ClassName,
        val beginStateWrapperClass: ClassName,
        val endViewSetClass: TypeName,
        val endStateClass: ClassName,
        val endStateWrapperClass: ClassName
)