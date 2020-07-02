package name.wildswift.mapache.generator.generatemodel

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName

data class ViewContentDefinition(
        val typeName: ClassName,
        val viewType: TypeName,
        val name: String?,
        val default: Boolean,
        val targetState: ClassName
) {
}