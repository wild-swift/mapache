package name.wildswift.mapache.generator.generatemodel

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName

data class TransitionDefinition(
        val typeName: ClassName,
        val wrapperTypeName: ClassName,
        val beginViewSetClass: TypeName,
        val beginStateClass: ClassName,
        val beginStateWrapperClass: ClassName,
        val endViewSetClass: TypeName,
        val endStateClass: ClassName,
        val endStateWrapperClass: ClassName,
        val viewRootType: TypeName
)